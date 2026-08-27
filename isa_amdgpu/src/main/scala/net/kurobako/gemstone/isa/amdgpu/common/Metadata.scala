package net.kurobako.gemstone.isa.amdgpu.common

enum MetadataSource {
  case MesaACO, AMDXML
}

enum Generation(val number: String) {
  case GFX6  extends Generation("gfx6")
  case GFX7  extends Generation("gfx7")
  case GFX8  extends Generation("gfx8")
  case GFX9  extends Generation("gfx9")
  case GFX10 extends Generation("gfx10")
  case GFX11 extends Generation("gfx11")
  case GFX12 extends Generation("gfx12")
  case CDNA1 extends Generation("cdna1")
  case CDNA2 extends Generation("cdna2")
  case CDNA3 extends Generation("cdna3")
  case CDNA4 extends Generation("cdna4")
  case CDNA5 extends Generation("cdna5")
}

/** Exact processor target used by LLVM MC. It selects the corresponding vendor metadata subfamily without cloning the
  * public parser/AST package for every processor.
  */
final case class Target private (name: String, generation: Generation, metadataTarget: String)

object Target {
  def fromString(value: String): Either[String, Target] = {
    val name = value.toLowerCase
    classify(name).toRight(s"unsupported AMDGPU target '$value'").map { case (generation, metadataTarget) =>
      Target(name, generation, metadataTarget)
    }
  }

  def unsafe(value: String): Target =
    fromString(value).fold(message => throw new IllegalArgumentException(message), identity)

  private def classify(name: String): Option[(Generation, String)] = name match {
    case "tahiti" | "verde" | "gfx600" | "gfx601"                         => Some(Generation.GFX6 -> "gfx6")
    case "bonaire" | "hawaii" | "kabini" | "kaveri" | "gfx700" | "gfx701" =>
      Some(Generation.GFX7 -> "gfx7")
    case "carrizo" | "fiji" | "iceland" | "stoney" | "tonga" | "gfx800" | "gfx801" | "gfx802" | "gfx803" |
        "gfx810" => Some(Generation.GFX8 -> "gfx8")
    case "gfx908"                                       => Some(Generation.CDNA1 -> "cdna1")
    case "gfx90a"                                       => Some(Generation.CDNA2 -> "cdna2")
    case "gfx940" | "gfx941" | "gfx942"                 => Some(Generation.CDNA3 -> "cdna3")
    case "gfx950"                                       => Some(Generation.CDNA4 -> "cdna4")
    case "gfx9" | "gfx900" | "gfx904" | "gfx906"        => Some(Generation.GFX9 -> "gfx9")
    case value if value.matches("gfx10(0[0-9]|1[0-9])") => Some(Generation.GFX10 -> "rdna1")
    case value if value.matches("gfx10[3-9][0-9]")      => Some(Generation.GFX10 -> "rdna2")
    case value if value.matches("gfx110[0-9]")          => Some(Generation.GFX11 -> "rdna3")
    case value if value.matches("gfx11(5[0-9]|7[0-9])") => Some(Generation.GFX11 -> "rdna3_5")
    case "gfx12" | "gfx1200" | "gfx1250" | "gfx1251"    => Some(Generation.GFX12 -> "rdna4")
    case _                                              => None
  }
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
    generation: Generation,
    target: String,
    format: String,
    opcode: Int,
    operands: Vector[OperandInfo],
    functionalGroup: Option[String],
    source: MetadataSource
)

final case class Metadata(encodings: Vector[Encoding]) {
  def supports(generation: Generation): Boolean = encodings.exists(_.generation == generation)
  def supports(target: Target): Boolean         =
    encodings.exists(encoding => encoding.generation == target.generation && encoding.target == target.metadataTarget)
}

private[amdgpu] def instructionsFor(generation: Generation): Vector[InstructionDef] =
  Instructions.iterator.filter(_.meta.supports(generation)).toVector

private[amdgpu] def encodingsFor(definition: InstructionDef, generation: Generation): Vector[Encoding] =
  definition.meta.encodings.filter(_.generation == generation)

private[amdgpu] def encodingsFor(definition: InstructionDef, target: Target): Vector[Encoding] =
  definition.meta.encodings.filter(encoding =>
    encoding.generation == target.generation && encoding.target == target.metadataTarget
  )

private[amdgpu] def encodingsFor(instruction: Instruction, target: Target): Vector[Encoding] =
  encodingsFor(instruction.definition, target)

private[amdgpu] lazy val allEncodings: Vector[Encoding] =
  Instructions.iterator.map(_.meta).toVector.distinct.flatMap(_.encodings)
