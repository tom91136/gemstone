package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

enum MetadataSource {
  case MesaACO, AMDXML
}

enum Family(val number: String) {
  case GFX6  extends Family("gfx6")
  case GFX7  extends Family("gfx7")
  case GFX8  extends Family("gfx8")
  case GFX9  extends Family("gfx9")
  case GFX10 extends Family("gfx10")
  case GFX11 extends Family("gfx11")
  case GFX12 extends Family("gfx12")
  case CDNA1 extends Family("cdna1")
  case CDNA2 extends Family("cdna2")
  case CDNA3 extends Family("cdna3")
  case CDNA4 extends Family("cdna4")
  case CDNA5 extends Family("cdna5")
}

/** Exact processor target used by LLVM MC. It selects the corresponding vendor metadata subfamily without cloning the
  * public parser/AST package for every processor.
  */
final case class Target private (name: String, family: Family, metadataTarget: String)

object Target {
  def fromString(value: String): Either[String, Target] = {
    val name = value.toLowerCase
    classify(name).toRight(s"unsupported AMDGPU target '$value'").map { case (family, metadataTarget) =>
      Target(name, family, metadataTarget)
    }
  }

  def unsafe(value: String): Target =
    fromString(value).fold(message => throw IllegalArgumentException(message), identity)

  private def classify(name: String): Option[(Family, String)] = name match {
    case "tahiti" | "verde" | "gfx600" | "gfx601"                         => Some(Family.GFX6 -> "gfx6")
    case "bonaire" | "hawaii" | "kabini" | "kaveri" | "gfx700" | "gfx701" =>
      Some(Family.GFX7 -> "gfx7")
    case "carrizo" | "fiji" | "iceland" | "stoney" | "tonga" | "gfx800" | "gfx801" | "gfx802" | "gfx803" |
        "gfx810" => Some(Family.GFX8 -> "gfx8")
    case "gfx908"                                                   => Some(Family.CDNA1 -> "cdna1")
    case "gfx90a"                                                   => Some(Family.CDNA2 -> "cdna2")
    case "gfx940" | "gfx941" | "gfx942"                             => Some(Family.CDNA3 -> "cdna3")
    case "gfx950"                                                   => Some(Family.CDNA4 -> "cdna4")
    case "gfx9" | "gfx900" | "gfx904" | "gfx906"                    => Some(Family.GFX9 -> "gfx9")
    case s"gfx10$suffix" if twoDigits(suffix) && suffix.toInt <= 19 => Some(Family.GFX10 -> "rdna1")
    case s"gfx10$suffix" if twoDigits(suffix) && suffix.head >= '3' => Some(Family.GFX10 -> "rdna2")
    case s"gfx110$digit" if oneDigit(digit)                         => Some(Family.GFX11 -> "rdna3")
    case s"gfx11$suffix" if twoDigits(suffix) && (suffix.head == '5' || suffix.head == '7') =>
      Some(Family.GFX11 -> "rdna3_5")
    case "gfx12" | "gfx1200" | "gfx1250" | "gfx1251" => Some(Family.GFX12 -> "rdna4")
    case _                                           => None
  }

  private def oneDigit(value: String): Boolean  = value.length == 1 && value.head.isDigit
  private def twoDigits(value: String): Boolean = value.length == 2 && value.forall(_.isDigit)
}

final case class OperandInfo(
    field: Option[String],
    kind: String,
    sizeBits: Int,
    input: Boolean,
    output: Boolean,
    implicitOperand: Boolean
)

final case class Encoding(
    family: Family,
    target: String,
    format: String,
    opcode: Int,
    operands: Col[OperandInfo],
    functionalGroup: Option[String],
    source: MetadataSource
)

final case class Metadata(encodings: Col[Encoding]) {
  def supports(family: Family): Boolean = encodings.exists(_.family == family)
  def supports(target: Target): Boolean =
    encodings.exists(encoding => encoding.family == target.family && encoding.target == target.metadataTarget)
}

private[amdgpu] def instructionsFor(family: Family): Col[InstructionDef] =
  Instruction.definitions.iterator.filter(_.meta.supports(family)).toCol

private[amdgpu] def encodingsFor(definition: InstructionDef, family: Family): Col[Encoding] =
  definition.meta.encodings.filter(_.family == family)

private[amdgpu] def encodingsFor(definition: InstructionDef, target: Target): Col[Encoding] =
  definition.meta.encodings.filter(encoding =>
    encoding.family == target.family && encoding.target == target.metadataTarget
  )

private[amdgpu] def encodingsFor(instruction: Instruction, target: Target): Col[Encoding] =
  encodingsFor(instruction.definition, target)

private[amdgpu] lazy val allEncodings: Col[Encoding] =
  Instruction.definitions.iterator.map(_.meta).toCol.distinct.flatMap(_.encodings)
