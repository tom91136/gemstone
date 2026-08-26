package net.kurobako.gemstone.isa.amdgpu.common

enum MetadataSource {
  case MesaAco, AmdXml
}

enum Generation(val number: String) {
  case Gfx6  extends Generation("gfx6")
  case Gfx7  extends Generation("gfx7")
  case Gfx8  extends Generation("gfx8")
  case Gfx9  extends Generation("gfx9")
  case Gfx10 extends Generation("gfx10")
  case Gfx11 extends Generation("gfx11")
  case Gfx12 extends Generation("gfx12")
  case Cdna1 extends Generation("cdna1")
  case Cdna2 extends Generation("cdna2")
  case Cdna3 extends Generation("cdna3")
  case Cdna4 extends Generation("cdna4")
  case Cdna5 extends Generation("cdna5")
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
    case "tahiti" | "verde" | "gfx600" | "gfx601"                         => Some(Generation.Gfx6 -> "gfx6")
    case "bonaire" | "hawaii" | "kabini" | "kaveri" | "gfx700" | "gfx701" =>
      Some(Generation.Gfx7 -> "gfx7")
    case "carrizo" | "fiji" | "iceland" | "stoney" | "tonga" | "gfx800" | "gfx801" | "gfx802" | "gfx803" |
        "gfx810" => Some(Generation.Gfx8 -> "gfx8")
    case "gfx908"                                       => Some(Generation.Cdna1 -> "cdna1")
    case "gfx90a"                                       => Some(Generation.Cdna2 -> "cdna2")
    case "gfx940" | "gfx941" | "gfx942"                 => Some(Generation.Cdna3 -> "cdna3")
    case "gfx950"                                       => Some(Generation.Cdna4 -> "cdna4")
    case "gfx9" | "gfx900" | "gfx904" | "gfx906"        => Some(Generation.Gfx9 -> "gfx9")
    case value if value.matches("gfx10(0[0-9]|1[0-9])") => Some(Generation.Gfx10 -> "rdna1")
    case value if value.matches("gfx10[3-9][0-9]")      => Some(Generation.Gfx10 -> "rdna2")
    case value if value.matches("gfx110[0-9]")          => Some(Generation.Gfx11 -> "rdna3")
    case value if value.matches("gfx11(5[0-9]|7[0-9])") => Some(Generation.Gfx11 -> "rdna3_5")
    case "gfx12" | "gfx1200" | "gfx1250" | "gfx1251"    => Some(Generation.Gfx12 -> "rdna4")
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

final case class InstructionInfo(
    name: String,
    generation: Generation,
    target: String,
    format: String,
    opcode: Int,
    operands: Vector[OperandInfo],
    functionalGroup: Option[String],
    source: MetadataSource
)

/** Complete normalized metadata across every supported AMD architectural family. */
trait InstructionDatabase {
  def instructions: Vector[InstructionInfo]
  def soppEncodingPrefix: Long
  def soppOpcodeShift: Int
  def soppImmediateBits: Int

  lazy val byName: Map[String, Vector[InstructionInfo]] = instructions.groupBy(_.name)

  def lookup(name: String, generation: Generation): Vector[InstructionInfo] =
    byName.getOrElse(name.toLowerCase, Vector.empty).filter(_.generation == generation)

  def lookup(name: String, target: Target): Vector[InstructionInfo] =
    byName.getOrElse(name.toLowerCase, Vector.empty).filter { instruction =>
      instruction.generation == target.generation && instruction.target == target.metadataTarget
    }
}

/** A catalog bound to the architectural family named by its package. */
trait InstructionCatalog {
  def version: String
  def instructions: Vector[InstructionInfo]

  lazy val byName: Map[String, Vector[InstructionInfo]] = instructions.groupBy(_.name)

  final def lookup(name: String): Vector[InstructionInfo] = byName.getOrElse(name.toLowerCase, Vector.empty)
}

private[amdgpu] final class GenerationCatalog(generation: Generation, database: InstructionDatabase)
    extends InstructionCatalog {
  override val version: String                            = generation.number
  override lazy val instructions: Vector[InstructionInfo] = database.instructions.filter(_.generation == generation)
}
