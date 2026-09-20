package net.kurobako.gemstone.isa.amdgpu

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.nio.charset.StandardCharsets
import java.util.Base64
import net.kurobako.gemstone.NoNul
import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import scala.util.Using

enum MetadataSource {
  case MesaACO, AMDXML
}

type OperandKind = OperandKind.T
object OperandKind extends RefinedType[String, MinLength[1] & NoNul]

type MetadataTarget = MetadataTarget.T
object MetadataTarget extends RefinedType[String, MinLength[1] & NoNul]

type InstructionFormat = InstructionFormat.T
object InstructionFormat extends RefinedType[String, MinLength[1] & NoNul]

type FunctionalGroup = FunctionalGroup.T
object FunctionalGroup extends RefinedType[String, MinLength[1] & NoNul]

enum Family(val number: String :| MinLength[1]) {
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

final class SupportedAMDGPUTarget
object SupportedAMDGPUTarget {
  inline given Constraint[String, SupportedAMDGPUTarget] with {
    override inline def test(inline value: String): Boolean = Target.isSupported(value)
    override inline def message: String                     = "Value must be a supported AMDGPU processor target"
  }

  given (SupportedAMDGPUTarget ==> (MinLength[1] & NoNul)) = Implication()
}

/** Exact processor target used by LLVM MC. It selects the corresponding vendor metadata subfamily without cloning the
  * public parser/AST package for every processor.
  */
type Target = Target.T
object Target extends RefinedType[String, SupportedAMDGPUTarget] {
  extension (target: Target) {
    def name: String :| (MinLength[1] & NoNul) = target.value
    def family: Family                         = classification(target)._1
    def metadataTarget: MetadataTarget         = MetadataTarget.applyUnsafe(classification(target)._2)
  }

  private def classification(target: Target): (Family, String) =
    classify(target.value).getOrElse(throw MatchError(target.value))

  private[amdgpu] inline def isSupported(inline name: String): Boolean =
    (name == "tahiti") ||
      (name == "verde") ||
      (name == "gfx600") ||
      (name == "gfx601") ||
      (name == "bonaire") ||
      (name == "hawaii") ||
      (name == "kabini") ||
      (name == "kaveri") ||
      (name == "gfx700") ||
      (name == "gfx701") ||
      (name == "carrizo") ||
      (name == "fiji") ||
      (name == "iceland") ||
      (name == "stoney") ||
      (name == "tonga") ||
      (name == "gfx800") ||
      (name == "gfx801") ||
      (name == "gfx802") ||
      (name == "gfx803") ||
      (name == "gfx810") ||
      (name == "gfx908") ||
      (name == "gfx90a") ||
      (name == "gfx940") ||
      (name == "gfx941") ||
      (name == "gfx942") ||
      (name == "gfx950") ||
      (name == "gfx9") ||
      (name == "gfx900") ||
      (name == "gfx904") ||
      (name == "gfx906") ||
      (name == "gfx12") ||
      (name == "gfx1200") ||
      (name == "gfx1250") ||
      (name == "gfx1251") ||
      (name == "gfx1000") ||
      (name == "gfx1001") ||
      (name == "gfx1002") ||
      (name == "gfx1003") ||
      (name == "gfx1004") ||
      (name == "gfx1005") ||
      (name == "gfx1006") ||
      (name == "gfx1007") ||
      (name == "gfx1008") ||
      (name == "gfx1009") ||
      (name == "gfx1010") ||
      (name == "gfx1011") ||
      (name == "gfx1012") ||
      (name == "gfx1013") ||
      (name == "gfx1014") ||
      (name == "gfx1015") ||
      (name == "gfx1016") ||
      (name == "gfx1017") ||
      (name == "gfx1018") ||
      (name == "gfx1019") ||
      (name == "gfx1030") ||
      (name == "gfx1031") ||
      (name == "gfx1032") ||
      (name == "gfx1033") ||
      (name == "gfx1034") ||
      (name == "gfx1035") ||
      (name == "gfx1036") ||
      (name == "gfx1037") ||
      (name == "gfx1038") ||
      (name == "gfx1039") ||
      (name == "gfx1040") ||
      (name == "gfx1041") ||
      (name == "gfx1042") ||
      (name == "gfx1043") ||
      (name == "gfx1044") ||
      (name == "gfx1045") ||
      (name == "gfx1046") ||
      (name == "gfx1047") ||
      (name == "gfx1048") ||
      (name == "gfx1049") ||
      (name == "gfx1050") ||
      (name == "gfx1051") ||
      (name == "gfx1052") ||
      (name == "gfx1053") ||
      (name == "gfx1054") ||
      (name == "gfx1055") ||
      (name == "gfx1056") ||
      (name == "gfx1057") ||
      (name == "gfx1058") ||
      (name == "gfx1059") ||
      (name == "gfx1060") ||
      (name == "gfx1061") ||
      (name == "gfx1062") ||
      (name == "gfx1063") ||
      (name == "gfx1064") ||
      (name == "gfx1065") ||
      (name == "gfx1066") ||
      (name == "gfx1067") ||
      (name == "gfx1068") ||
      (name == "gfx1069") ||
      (name == "gfx1070") ||
      (name == "gfx1071") ||
      (name == "gfx1072") ||
      (name == "gfx1073") ||
      (name == "gfx1074") ||
      (name == "gfx1075") ||
      (name == "gfx1076") ||
      (name == "gfx1077") ||
      (name == "gfx1078") ||
      (name == "gfx1079") ||
      (name == "gfx1080") ||
      (name == "gfx1081") ||
      (name == "gfx1082") ||
      (name == "gfx1083") ||
      (name == "gfx1084") ||
      (name == "gfx1085") ||
      (name == "gfx1086") ||
      (name == "gfx1087") ||
      (name == "gfx1088") ||
      (name == "gfx1089") ||
      (name == "gfx1090") ||
      (name == "gfx1091") ||
      (name == "gfx1092") ||
      (name == "gfx1093") ||
      (name == "gfx1094") ||
      (name == "gfx1095") ||
      (name == "gfx1096") ||
      (name == "gfx1097") ||
      (name == "gfx1098") ||
      (name == "gfx1099") ||
      (name == "gfx1100") ||
      (name == "gfx1101") ||
      (name == "gfx1102") ||
      (name == "gfx1103") ||
      (name == "gfx1104") ||
      (name == "gfx1105") ||
      (name == "gfx1106") ||
      (name == "gfx1107") ||
      (name == "gfx1108") ||
      (name == "gfx1109") ||
      (name == "gfx1150") ||
      (name == "gfx1151") ||
      (name == "gfx1152") ||
      (name == "gfx1153") ||
      (name == "gfx1154") ||
      (name == "gfx1155") ||
      (name == "gfx1156") ||
      (name == "gfx1157") ||
      (name == "gfx1158") ||
      (name == "gfx1159") ||
      (name == "gfx1170") ||
      (name == "gfx1171") ||
      (name == "gfx1172") ||
      (name == "gfx1173") ||
      (name == "gfx1174") ||
      (name == "gfx1175") ||
      (name == "gfx1176") ||
      (name == "gfx1177") ||
      (name == "gfx1178") ||
      (name == "gfx1179")

  private[amdgpu] def classify(name: String): Option[(Family, String)] = name match {
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

  private inline def digit(inline value: Char): Boolean       = value >= '0' && value <= '9'
  private inline def oneDigit(inline value: String): Boolean  = value.length == 1 && digit(value.head)
  private inline def twoDigits(inline value: String): Boolean =
    value.length == 2 && digit(value.charAt(0)) && digit(value.charAt(1))
}

final case class OperandInfo(
    field: Option[String :| (MinLength[1] & NoNul)],
    kind: OperandKind,
    sizeBits: Int :| Positive,
    input: Boolean,
    output: Boolean,
    implicitOperand: Boolean
)

final case class Encoding(
    family: Family,
    target: MetadataTarget,
    format: InstructionFormat,
    opcode: Int :| Positive0,
    operands: Vec[OperandInfo],
    functionalGroup: Option[FunctionalGroup],
    source: MetadataSource
)

final case class Metadata(encodings: Vec[Encoding]) {
  def supports(family: Family): Boolean = encodings.exists(_.family == family)
  def supports(target: Target): Boolean =
    encodings.exists(encoding => encoding.family == target.family && encoding.target == target.metadataTarget)
}

private[amdgpu] object MetadataCatalogCodec {
  private val Magic = 0x474d4301

  def decode(encoded: String, expectedMetadataCount: Int, expectedEncodingCount: Int): Vec[Metadata] = {
    val bytes = Base64.getDecoder.decode(encoded)
    Using.resource(DataInputStream(ByteArrayInputStream(bytes))) { input =>
      val magic = input.readInt()
      if (magic != Magic) throw IllegalArgumentException(f"invalid AMDGPU metadata catalog magic 0x$magic%08x")
      val strings: Vector[String :| (MinLength[1] & NoNul)] = Vector.fill(readCount(input, "string")) {
        val raw = Array.ofDim[Byte](readCount(input, "string byte"))
        input.readFully(raw)
        String(raw, StandardCharsets.UTF_8)
          .refineEither[MinLength[1] & NoNul]
          .fold(
            detail => throw IllegalArgumentException(s"invalid AMDGPU metadata catalog string: $detail"),
            value => value
          )
      }
      val schemas = Vector.fill(readCount(input, "operand schema")) {
        Vector.fill(readCount(input, "operand")) {
          val fieldIndex = input.readInt()
          val kind       = OperandKind.applyUnsafe(strings(readIndex(input, strings.size, "operand kind")))
          val sizeBits   = readPositive(input, "operand bit width")
          val flags      = input.readUnsignedByte()
          if ((flags & ~0x7) != 0)
            throw IllegalArgumentException(s"invalid AMDGPU operand flags: $flags")
          OperandInfo(
            field = optional(strings, fieldIndex, "operand field"),
            kind = kind,
            sizeBits = sizeBits,
            input = (flags & 1) != 0,
            output = (flags & 2) != 0,
            implicitOperand = (flags & 4) != 0
          )
        }.toVec
      }
      val metadata = Vector.fill(readCount(input, "metadata")) {
        Metadata(
          Vector.fill(readCount(input, "encoding")) {
            Encoding(
              family = family(strings(readIndex(input, strings.size, "family"))),
              target = MetadataTarget.applyUnsafe(strings(readIndex(input, strings.size, "target"))),
              format = InstructionFormat.applyUnsafe(strings(readIndex(input, strings.size, "format"))),
              opcode = readCount(input, "opcode"),
              operands = schemas(readIndex(input, schemas.size, "operand schema")),
              functionalGroup = optional(strings, input.readInt(), "functional group").map(value =>
                FunctionalGroup.applyUnsafe(value)
              ),
              source = metadataSource(strings(readIndex(input, strings.size, "metadata source")))
            )
          }.toVec
        )
      }.toVec
      if (input.available() != 0)
        throw IllegalArgumentException(s"AMDGPU metadata catalog has ${input.available()} trailing bytes")
      if (metadata.size != expectedMetadataCount)
        throw IllegalArgumentException(
          s"AMDGPU metadata catalog contains ${metadata.size} entries, expected $expectedMetadataCount"
        )
      val encodingCount = metadata.iterator.map(_.encodings.size).sum
      if (encodingCount != expectedEncodingCount)
        throw IllegalArgumentException(
          s"AMDGPU metadata catalog contains $encodingCount encodings, expected $expectedEncodingCount"
        )
      metadata
    }
  }

  private def family(name: String): Family =
    Family.values.find(_.toString == name).getOrElse {
      throw IllegalArgumentException(s"unknown AMDGPU metadata family: $name")
    }

  private def metadataSource(name: String): MetadataSource =
    MetadataSource.values.find(_.toString == name).getOrElse {
      throw IllegalArgumentException(s"unknown AMDGPU metadata source: $name")
    }

  private def readCount(input: DataInputStream, label: String): Int :| Positive0 = {
    val value = input.readInt()
    value.refineEither[Positive0].fold(
      _ => throw IllegalArgumentException(s"negative AMDGPU $label count: $value"),
      refined => refined
    )
  }

  private def readPositive(input: DataInputStream, label: String): Int :| Positive = {
    val value = input.readInt()
    value.refineEither[Positive].fold(
      _ => throw IllegalArgumentException(s"non-positive AMDGPU $label: $value"),
      refined => refined
    )
  }

  private def readIndex(input: DataInputStream, limit: Int, label: String): Int = {
    val index = input.readInt()
    if (index < 0 || index >= limit)
      throw IllegalArgumentException(s"AMDGPU $label index $index is outside 0 until $limit")
    index
  }

  private def optional(
      values: Vector[String :| (MinLength[1] & NoNul)],
      index: Int,
      label: String
  ): Option[String :| (MinLength[1] & NoNul)] =
    if (index == -1) None
    else if (index >= 0 && index < values.size) Some(values(index))
    else throw IllegalArgumentException(s"AMDGPU $label index $index is outside -1 until ${values.size}")
}

private[amdgpu] def instructionsFor(family: Family): Vec[InstructionDef] =
  Instruction.definitions.iterator.filter(_.meta.supports(family)).toVec

private[amdgpu] def encodingsFor(definition: InstructionDef, family: Family): Vec[Encoding] =
  definition.meta.encodings.filter(_.family == family)

private[amdgpu] def encodingsFor(definition: InstructionDef, target: Target): Vec[Encoding] =
  definition.meta.encodings.filter(encoding =>
    encoding.family == target.family && encoding.target == target.metadataTarget
  )

private[amdgpu] def encodingsFor(instruction: Instruction, target: Target): Vec[Encoding] =
  encodingsFor(instruction.definition, target)

private[amdgpu] lazy val allEncodings: Vec[Encoding] =
  Instruction.definitions.iterator.map(_.meta).toVec.distinct.flatMap(_.encodings)
