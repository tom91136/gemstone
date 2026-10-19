package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.string.*
import org.w3c.dom.Element
import org.w3c.dom.Node
import scala.jdk.CollectionConverters.*
import scala.sys.process.Process
import scala.sys.process.ProcessLogger
import scala.util.Using
import upickle.default.*

/** Builds AMDGPU metadata from pinned AMD XML, LLVM TableGen and Mesa ACO inputs. */
object GenerateAMDGPUISA {
  private case class Request(
      python: String,
      tablegen: Path,
      references: Path,
      sourceRoot: Path,
      resourceRoot: Path,
      nativeRoot: Path
  )
  private val ACOExporter =
    """import importlib.util
import pathlib
import sys

path = pathlib.Path(sys.argv[1])
spec = importlib.util.spec_from_file_location("gemstone_pinned_aco_opcodes", path)
if spec is None or spec.loader is None:
    raise RuntimeError("cannot load pinned ACO opcode source")
module = importlib.util.module_from_spec(spec)
spec.loader.exec_module(module)

def operand(value, is_input, is_output):
    kind = value.base_type.name.removeprefix("aco_base_type_")
    return ",".join((kind, str(value.bit_size), str(value.num_components),
                     "1" if is_input else "0", "1" if is_output else "0",
                     "1" if value.fixed_reg.name != "not_fixed" else "0"))

for name in sorted(module.instructions):
    instruction = module.instructions[name]
    if instruction.format.name.startswith("PSEUDO"):
        continue
    operands = [operand(value, False, True) for value in instruction.definitions]
    operands.extend(operand(value, True, False) for value in instruction.operands)
    for generation in ("gfx6", "gfx7", "gfx8", "gfx9", "gfx10", "gfx11_7"):
        opcode = getattr(instruction.op, generation)
        if opcode >= 0:
            print("\t".join(({"gfx10": "RDNA1", "gfx11_7": "GFX11_7"}.get(generation, generation.upper()), name, instruction.format.name, str(opcode),
                              instruction.cls.value, ";".join(operands), "mesa-aco")))
"""
  case class Operand(
      field: String,
      kind: String,
      input: Boolean,
      output: Boolean,
      implicitOperand: Boolean,
      bits: Int
  )
  case class Instruction(
      name: String,
      isa: String,
      format: String,
      opcode: Int,
      operands: Vector[Operand],
      groups: Vector[String] = Vector.empty,
      processors: Vector[String] = Vector.empty
  )
  private case class Extracted(
      records: Vector[Instruction],
      layouts: Vector[ujson.Value],
      operands: Vector[ujson.Value]
  )
  private case class ComparisonClassification(kind: String, relation: String, updateExec: Boolean)
  private enum DefinitionDomain(val directory: String, val stem: String) derives CanEqual {
    case SOP1          extends DefinitionDomain("scalar", "SOP1")
    case SOP2          extends DefinitionDomain("scalar", "SOP2")
    case SOPC          extends DefinitionDomain("scalar", "SOPC")
    case SOPK          extends DefinitionDomain("scalar", "SOPK")
    case SOPP          extends DefinitionDomain("scalar", "SOPP")
    case ScalarMemory  extends DefinitionDomain("scalar", "ScalarMemory")
    case VOP1          extends DefinitionDomain("vector", "VOP1")
    case VOP2          extends DefinitionDomain("vector", "VOP2")
    case VOP3          extends DefinitionDomain("vector", "VOP3")
    case PackedVector  extends DefinitionDomain("vector", "PackedVector")
    case VectorCompare extends DefinitionDomain("vector", "VectorCompare")
    case DualVector    extends DefinitionDomain("vector", "DualVector")
    case Interpolation extends DefinitionDomain("vector", "Interpolation")
    case Matrix        extends DefinitionDomain("vector", "Matrix")
    case DataShare     extends DefinitionDomain("memory", "DataShare")
    case Buffer        extends DefinitionDomain("memory", "Buffer")
    case Flat          extends DefinitionDomain("memory", "Flat")
    case Image         extends DefinitionDomain("memory", "Image")
    case Export        extends DefinitionDomain("system", "Export")
    case LDSDirect     extends DefinitionDomain("system", "LDSDirect")
  }
  private case class DefinitionChunk(
      fileName: String,
      objectName: String,
      definitions: Vector[Definition]
  )
  private case class Definition(opcode: String, name: String)
  private case class InstructionSources(opcodes: String, metadata: String, definitions: Vector[(String, String)])

  private val MaximumDefinitionSourceCharacters = 750000
  private val PackageRoot                       = "net/kurobako/gemstone/isa/amdgpu"
  private val InstructionGroups                 = Set("ATOMIC", "WMMA", "SWMMAC")
  // Operand kinds whose field layouts the assembler and VM decode symbolically.
  private val SymbolicOperandKinds =
    Set("BUFFER_FORMAT", "DELAY", "HWREG", "MATRIX", "SENDMSG", "WAITCNT", "WAITCNT_DEPCTR", "WAIT_ALU", "WAIT_EVENT")

  private val TableSelection = Map(
    "FeatureSet"                    -> Vector("Features"),
    "Processor"                     -> Vector("Name", "Features"),
    "SubtargetFeature"              -> Vector("Implies"),
    "GCNSubtargetFeatureGeneration" -> Vector.empty,
    "VOP3_Pseudo"                   -> Vector("OpName", "IsWMMA", "IsSWMMAC"),
    "AMDGPUDimProps"                -> Vector("Encoding", "AsmSuffix", "NumCoords", "NumGradients"),
    "MIMG"                          -> Vector("BaseOpcode", "VAddrDwords", "VDataDwords"),
    "MIMGBaseOpcode"                -> Vector(
      "Store",
      "Atomic",
      "Sampler",
      "Gather4",
      "NumExtraArgs",
      "Gradients",
      "G16",
      "Coordinates",
      "LodOrClampOrMip",
      "BVH"
    )
  )
  private val MatrixFlags      = Vector("IsWMMA" -> "WMMA", "IsSWMMAC" -> "SWMMAC")
  private val ProcessorAliases = Vector("gfx1001" -> "gfx1010", "gfx12" -> "gfx1200")
  private val OpcodeFields     = Set("OP", "OPX", "OPY")
  private val CarryNames       = Map(
    "v_addc_co_u32"    -> "v_add_co_ci_u32",
    "v_subb_co_u32"    -> "v_sub_co_ci_u32",
    "v_subbrev_co_u32" -> "v_subrev_co_ci_u32"
  )
  private val Gfx117Parents     = Set("RDNA3_5", "RDNA4")
  private val PreGfx11Parents   = Set("CDNA1", "RDNA1", "RDNA2")
  private val DocumentedFormats = Set(
    "SOP1",
    "SOP2",
    "SOPC",
    "SOPP",
    "SOPK",
    "VOP1",
    "VOP2",
    "VOPC",
    "VOP3",
    "VOP3P",
    "DS",
    "MUBUF",
    "MTBUF",
    "FLAT",
    "FLAT_GLBL",
    "FLAT_SCRATCH",
    "SMEM",
    "EXP",
    "MIMG",
    "VINTRP"
  )
  private val VopFormats        = Set("VOP1", "VOP2", "VOPC")
  private val FallbackFormats   = Set("VOP1", "VOP2", "VOPC", "VOP3", "VINTRP")
  private val Vop3Formats       = Set("VOP3", "VOP3_SDST_ENC")
  private val RetiredOperations = Map(
    "v_min_legacy_f32" -> "v_min_f32",
    "v_max_legacy_f32" -> "v_max_f32",
    "v_lshl_b32"       -> "v_lshlrev_b32",
    "v_lshr_b32"       -> "v_lshrrev_b32",
    "v_ashr_i32"       -> "v_ashrrev_i32",
    "v_rcp_legacy_f32" -> "v_rcp_f32",
    "v_rcp_clamp_f32"  -> "v_rcp_f32",
    "v_rsq_legacy_f32" -> "v_rsq_f32",
    "v_rsq_clamp_f32"  -> "v_rsq_f32",
    "v_log_clamp_f32"  -> "v_log_f32",
    "v_rcp_clamp_f64"  -> "v_rcp_f64",
    "v_rsq_clamp_f64"  -> "v_rsq_f64",
    "v_exp_legacy_f32" -> "v_exp_f32",
    "v_log_legacy_f32" -> "v_log_f32"
  )
  private val LegacyGenerations   = Vector("GFX6", "GFX7", "GFX8", "GFX9")
  private val LegacyGenerationSet = LegacyGenerations.toSet
  private val EarliestGenerations = Set("GFX6", "GFX7")
  private val SignedCarryNames    = Map(
    "v_add_co_u32"       -> "v_add_i32",
    "v_sub_co_u32"       -> "v_sub_i32",
    "v_subrev_co_u32"    -> "v_subrev_i32",
    "v_add_co_ci_u32"    -> "v_addc_u32",
    "v_sub_co_ci_u32"    -> "v_subb_u32",
    "v_subrev_co_ci_u32" -> "v_subbrev_u32"
  )
  private val CarryAliasNames = Map(
    "v_add_co_ci_u32"    -> "v_addc_co_u32",
    "v_sub_co_ci_u32"    -> "v_subb_co_u32",
    "v_subrev_co_ci_u32" -> "v_subbrev_co_u32"
  )
  private val Gfx8Names = Map(
    "v_add_co_u32"    -> "v_add_u32",
    "v_sub_co_u32"    -> "v_sub_u32",
    "v_subrev_co_u32" -> "v_subrev_u32"
  )
  private val CdnaGenerations      = Vector("CDNA1", "CDNA2", "CDNA3", "CDNA4")
  private val CdnaGenerationSet    = CdnaGenerations.toSet
  private val VopOrVop3            = Set("VOP2", "VOP3")
  private val LaterCdnaGenerations = Set("CDNA2", "CDNA3", "CDNA4")
  private val SignedDotGenerations = Set("RDNA3", "RDNA3_5", "GFX11_7", "RDNA4")
  private val IllegalGenerations   = Vector("RDNA1", "RDNA2", "RDNA3", "RDNA3_5", "RDNA4", "GFX11_7", "CDNA5")
  private val DataShareFormats     = Set("DS", "VDS")
  private val ScopeNames           = Vector("CU", "SE", "DEV", "SYS")
  private val FixedRegisters       = Vector(
    "VccLo"           -> "vcc_lo",
    "VccHi"           -> "vcc_hi",
    "M0"              -> "m0",
    "NullSgpr"        -> "null",
    "ExecLo"          -> "exec_lo",
    "ExecHi"          -> "exec_hi",
    "SccSource"       -> "src_scc",
    "LdsDirectSource" -> "src_lds_direct"
  )
  private val BufferFormatIsas = Vector(
    "GFX6",
    "GFX7",
    "GFX8",
    "GFX9",
    "CDNA1",
    "CDNA2",
    "CDNA3",
    "CDNA4",
    "CDNA5",
    "RDNA1",
    "RDNA2",
    "RDNA3",
    "RDNA3_5",
    "RDNA4",
    "GFX11_7"
  )
  private val GfxTenUnifiedIsas = Set("RDNA1", "RDNA2")
  private val MatrixEnums       = Vector(
    ("FMT", "MatrixFMT", "MATRIX_FMT_", 3),
    ("SCALE", "MatrixScale", "MATRIX_SCALE_", 1),
    ("SCALE_FMT", "MatrixScaleFmt", "MATRIX_SCALE_FMT_", 2)
  )
  private val ModernSourceIsas      = Set("RDNA4", "CDNA5", "GFX11_7")
  private val ImplicitSources       = Vector("SRC_VCCZ", "SRC_EXECZ", "SRC_SCC", "SRC_POPS_EXITING_WAVE_ID")
  private val TrapSources           = Vector("TBA_LO", "TBA_HI", "TMA_LO", "TMA_HI")
  private val ByteSelectConversions = Set("v_cvt_f32_bf8", "v_cvt_f32_fp8")
  private val ScalarFormats         = Set("SOP1", "SOP2", "SOPC")
  private val LiteralSuffixes       = Vector(4 -> "_INST_LITERAL", 8 -> "_INST_LITERAL64")
  private val ModernImageFormats    = Set("VIMAGE", "VSAMPLE")
  private val IndexedFields         = Vector(
    ("VectorSources", "VSRC", ""),
    ("ImageAddresses", "VADDR", ""),
    ("LaneSelectors", "LANE_SEL_", ""),
    ("ScalarSelectors", "S", "")
  )
  private val MatrixMnemonics      = Vector("mfma", "wmma", "swmma", "tensor")
  private val OperandConstantEnums = Vector(
    ("EncValues", "", "Source"),
    ("DPP", "DppCtrl", "DPP"),
    ("DPP", "DppFiMode", "DPP8"),
    ("SDWA", "SdwaSel", "SDWA"),
    ("SDWA", "DstUnused", "SDWAUnused"),
    ("Swizzle", "EncBits", "Swizzle"),
    ("SIOutMods", "", "OutputModifiers")
  )
  private val BarrierImmediates = Set("s_barrier_init_imm", "s_barrier_join_imm", "s_get_barrier_state_imm")
  private val BitOp3Dropped     = Set("ABS", "NEG", "OMOD")
  private val OpSelFields       = Set("OPSEL", "OP_SEL")
  private val ScaledFormats     = Set("VOP3PX2", "VOP3PX3")
  private val HwregIsas         = Set("RDNA4", "CDNA5")

  private val command = Command("GenerateAMDGPUISA", "Generate AMDGPU ISA sources")((
    Opts.option[String]("python", help = "Python executable for the pinned ACO model").withDefault("python3"),
    Opts.option[String]("tablegen", help = "Pinned LLVM TableGen executable").map(Path.of(_)),
    Opts.argument[String]("prepared-references").map(Path.of(_)),
    Opts.argument[String]("generated-source-root").map(Path.of(_)),
    Opts.argument[String]("generated-resource-root").map(Path.of(_)),
    Opts.argument[String]("generated-native-root").map(Path.of(_))
  ).mapN(Request(_, _, _, _, _, _)))

  private val Gfx9Version = "FeatureISAVersion9_0_[0-9A-F]".r

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    val result = extract(request.references, request.python)
    val llvm   = request.references.resolve("llvm/llvm")
    val tables = TableGen.records(
      executable = request.tablegen,
      source = llvm.resolve("lib/Target/AMDGPU/AMDGPU.td"),
      includes = Vector(llvm.resolve("include"), llvm.resolve("lib/Target/AMDGPU")),
      selection = TableSelection,
      python = request.python
    )
    val instructionRoot                 = request.sourceRoot.resolve(PackageRoot)
    val defaults                        = instructionRoot.resolve("TargetDefaults.scala")
    val (scalaDefaults, nativeDefaults) = renderTargetDefaults(tables)
    write(defaults, scalaDefaults)
    val _ = GeneratedFiles.write(request.nativeRoot.resolve("amdgpu/gemstone_amdgpu_target.hpp"), nativeDefaults)
    val constants = instructionRoot.resolve("EncodingConstants.scala")
    write(constants, renderConstants(result, request.references))
    val matrixGroups = tables("VOP3_Pseudo").obj.values.toVector.groupBy(_("OpName").str).map { case (name, values) =>
      name -> MatrixFlags.collect {
        case (flag, group) if values.exists(_(flag).num == 1) => group
      }
    }
    val dlProcessors = tables("FeatureSet").obj.toVector.collect {
      case (name, value)
          if Gfx9Version.matches(name) &&
            value("Features").arr.exists(_("def").str == "FeatureDLInsts") =>
        "gfx" + name.stripPrefix("FeatureISAVersion").replace("_", "").toLowerCaseRoot
    }.sorted
    require(dlProcessors.nonEmpty, "LLVM provides no GFX9 processors with DL instructions")
    val records = result.records.map(value =>
      value.copy(
        groups = (value.groups ++ matrixGroups.getOrElse(value.name, Vector.empty)).filter(InstructionGroups).distinct
          .sorted,
        processors = if (value.isa == "GFX9" && value.name == "v_fmac_f32") dlProcessors else value.processors
      )
    ).distinct
    val domains = renderDomains(result.copy(records = records)) :+ ("ImageDimension" -> renderImageDimensions(tables))
    domains.foreach { case (name, source) =>
      write(instructionRoot.resolve(s"$name.scala"), source)
    }
    val instructions = renderInstructions(records)
    write(instructionRoot.resolve("Opcode.scala"), instructions.opcodes)
    val definitionsRoot = instructionRoot.resolve("definitions")
    val definitions     = instructions.definitions.map { case (name, content) =>
      definitionsRoot.resolve(name) -> content
    }
    definitions.foreach { case (path, content) => write(path, content) }
    val metadataResource = request.resourceRoot.resolve(PackageRoot).resolve("metadata.json")
    val _                = GeneratedFiles.write(metadataResource, instructions.metadata)
    val layoutsResource  = metadataResource.resolveSibling("layouts.json")
    val _                = GeneratedFiles.write(
      layoutsResource,
      ujson.write(ujson.Arr.from(result.layouts.flatMap(layoutMetadata(_))), indent = 2) + "\n"
    )
    val operandsResource = metadataResource.resolveSibling("operands.json")
    val _                = GeneratedFiles.write(
      operandsResource,
      ujson.write(ujson.Arr.from(result.operands.flatMap(operandMetadata(_))), indent = 2) + "\n"
    )
    val imagesResource = metadataResource.resolveSibling("images.json")
    val opcodes        = records.map(_.name).toSet
    val images         = ujson.Obj.from(imageMetadata(tables)("instructions").obj.filter { case (name, _) =>
      opcodes(name)
    })
    val _        = GeneratedFiles.write(imagesResource, ujson.write(images, indent = 2) + "\n")
    val expected = definitions.map(_._1).toSet ++ Set(constants, defaults, instructionRoot.resolve("Opcode.scala")) ++
      domains.map(value => instructionRoot.resolve(s"${value._1}.scala"))
    val include = (path: Path) => path.getFileName.toString.endsWith(".scala")
    GeneratedFiles.removeUnexpected(request.sourceRoot, expected, include)
    GeneratedFiles.removeUnexpected(
      request.resourceRoot,
      Set(metadataResource, layoutsResource, operandsResource, imagesResource),
      _ => true
    )
  }

  def renderTargetDefaults(tables: ujson.Value): (scalaSource: String, nativeSource: String) = {
    val features    = tables("SubtargetFeature").obj
    val generations = tables("GCNSubtargetFeatureGeneration").obj.keySet.toSet
    @scala.annotation.tailrec
    def expand(enabled: Set[String]): Set[String] = {
      val next = enabled ++ enabled.flatMap(name => features(name)("Implies").arr.map(_("def").str))
      if (next == enabled) enabled else expand(next)
    }
    // LLVM defaults GCN processors without an explicit wave64 feature to wave32.
    val wave32 = tables("Processor").obj.values.toVector.collect {
      case processor if {
            val enabled = expand(processor("Features").arr.map(_("def").str).toSet)
            enabled("FeatureWavefrontSize32") || (enabled.exists(generations) && !enabled("FeatureWavefrontSize64"))
          } => processor("Name").str
    }.toSet
    require(wave32("gfx1200"), "LLVM processor defaults lack RDNA wave32 metadata")
    // Accepted legacy/family aliases absent from LLVM's processor records.
    val aliases = ProcessorAliases.collect {
      case (alias, processor) if wave32(processor) => alias
    }
    val names       = (wave32.toVector ++ aliases).sorted.map(Emission.literal)
    val scalaSource = GeneratedHeader.scala("from LLVM processor wavefront defaults") +
      s"""|package net.kurobako.gemstone.isa.amdgpu
          |
          |import net.kurobako.gemstone.constraint.*
          |
          |${GeneratedHeader.annotation()}object TargetDefaults {
          |  def waveSize(target: Target): Int in (32, 64) = target.name.raw match {
          |    case ${names.mkString(" | ")} => 32
          |    case _ => 64
          |  }
          |}
          |""".stripMargin
    val native = GeneratedHeader.cpp("from LLVM processor wavefront defaults") +
      s"""|#pragma once
          |#include <string_view>
          |
          |namespace gemstone::amdgpu {
          |inline unsigned defaultWaveSize(std::string_view target) noexcept {
          |  constexpr std::string_view wave32[] = {${names.mkString(", ")}};
          |  for (auto name : wave32) if (target == name) return 32;
          |  return 64;
          |}
          |} // namespace gemstone::amdgpu
          |""".stripMargin
    (scalaSource, native)
  }

  private def layoutMetadata(value: ujson.Value): Option[ujson.Value] = {
    val opcodes = value("fields").arr.filter(field => OpcodeFields(field("name").str))
      .flatMap(_("ranges").arr).foldLeft(BigInt(0)) { (bits, range) =>
        bits | (((BigInt(1) << range("bits").num.toInt) - 1) << range("offset").num.toInt)
      }
    val mask     = BigInt(value("mask").str, 16) & ~opcodes
    val prefixes = value("identifiers").arr.map(identifier => BigInt(identifier.str, 16) & mask).distinct
    // Layouts whose identifiers disagree outside the opcode fields have no single fixed prefix.
    Option.when(prefixes.size == 1)(ujson.Obj.from(value.obj.toVector.flatMap {
      case ("mask", _)        => Vector("mask" -> ujson.Str(mask.toString(16)))
      case ("identifiers", _) => Vector("prefix" -> ujson.Str(prefixes.head.toString(16)))
      case entry              => Vector(entry)
    }))
  }

  private def operandMetadata(value: ujson.Value): Option[ujson.Value] = {
    val selectors = value.obj.get("values").toVector.flatMap(_.obj.toVector).flatMap { case (name, code) =>
      code.str.toIntOption.map(name -> _)
    }
    val integers = selectors.flatMap { case (name, code) =>
      name.toLongOption.map(number => code.toString -> ujson.Str(number.toString))
    }
    val floating = selectors.filter(_._1.contains('.')).flatMap { case (name, code) =>
      name.toDoubleOption.map(number => code.toString -> ujson.Num(number))
    }
    val special = selectors.filter { case (name, _) => name.startsWith("src_") && !name.startsWith("src_literal") }
      .sortBy(_._1).reverse.map { case (name, code) => code.toString -> ujson.Str(name) }
    val literals = selectors.collect {
      case ("src_literal", code)   => code.toString -> ujson.Num(4)
      case ("src_literal64", code) => code.toString -> ujson.Num(8)
    }
    val header = Vector("isa" -> value("isa"), "kind" -> value("kind"))
    if (value("kind").str == "SRC") Some(ujson.Obj.from(header ++ Vector(
      "selectors" -> ujson.Obj.from(selectors.map { case (name, code) => name -> ujson.Num(code) }),
      "integers"  -> ujson.Obj.from(integers),
      "floating"  -> ujson.Obj.from(floating),
      "special"   -> ujson.Obj.from(special),
      "literals"  -> ujson.Obj.from(literals)
    )))
    else Option.when(SymbolicOperandKinds(value("kind").str))(ujson.Obj.from(header :+ ("fields" -> value("fields"))))
  }

  private def imageMetadata(tables: ujson.Value): ujson.Value = {
    def number(value: ujson.Value): Int = value match {
      case ujson.Arr(bits) => bits.zipWithIndex.map { case (bit, index) => bit.num.toInt << index }.sum
      case _               => value.num.toInt
    }
    val dimensions = tables("AMDGPUDimProps").obj.values.toVector.sortBy(value => number(value("Encoding"))).map {
      value =>
        ujson.Obj(
          "name"        -> value("AsmSuffix"),
          "coordinates" -> number(value("NumCoords")),
          "gradients"   -> number(value("NumGradients"))
        )
    }
    require(
      tables("AMDGPUDimProps").obj.values.map(value => number(value("Encoding"))).toSet == dimensions.indices.toSet,
      "noncontiguous image dimension encodings"
    )
    val bases        = tables("MIMGBaseOpcode").obj
    val machine      = tables("MIMG").obj.values.toVector.filterNot(_("BaseOpcode").isNull)
    val instructions = bases.toVector.sortBy(_._1).map { case (name, value) =>
      val properties = ujson.Obj.from(value.obj.toVector.map { case (field, data) =>
        val key = if (field == "BVH") "bvh" else field.head.toLower.toString + field.tail
        key -> (if (field == "NumExtraArgs") ujson.Num(number(data)) else ujson.Bool(number(data) != 0))
      })
      val rows                                                 = machine.filter(_("BaseOpcode")("def").str == name)
      def width(field: String, rows: Vector[ujson.Value]): Int = {
        val widths = rows.map(row => number(row(field))).distinct
        require(widths.size == 1, s"ambiguous image $name $field: $widths")
        widths.head
      }
      if (number(value("BVH")) != 0) {
        properties("bvhAddressWords") = width("VAddrDwords", rows)
        properties("bvhDataWords") = width("VDataDwords", rows)
        val packed = machine.filter(_("BaseOpcode")("def").str == s"${name}_a16")
        properties("bvhPackedAddressWords") =
          if (packed.nonEmpty) ujson.Num(width("VAddrDwords", packed)) else properties("bvhAddressWords")
      }
      name.toLowerCaseRoot -> properties
    }

    ujson.Obj("dimensions" -> ujson.Arr.from(dimensions), "instructions" -> ujson.Obj.from(instructions))
  }

  private val IllegalWord = raw"(?s)def V_ILLEGAL :.*?Inst\{31-0\} = 0x([0-9a-fA-F]+);".r

  private val HwregId = "ID_(DVGPR_ALLOC_(?:LO|HI)) = ([0-9]+)".r

  private val ReverseSubtractName = "(buffer|image)_atomic_rsub(_x2)?".r

  private val CompareName = "v_cmpx?_(lg|tru)_[iu][0-9]+".r

  private val FloatSuffix = "_f".r

  private val MinMaxName = "v_(dual_|pk_)?(min|max|min3|max3|med3|minmax|maxmin)_f(16|32|64)".r

  private def extract(references: Path, python: String): Extracted = {
    if (!Files.isRegularFile(references.resolve("MANIFEST.tsv")))
      throw IllegalArgumentException(s"not a prepared ISA reference directory: $references")
    val xmlRoot  = references.resolve("amd/xml")
    val xmlFiles = Using.resource(Files.list(xmlRoot)) { stream =>
      stream.toList.asScala.view.filter(path => path.getFileName.toString.endsWith(".xml")).toVector.sortBy(_.toString)
    }
    if (xmlFiles.size != 10)
      throw IllegalStateException(s"expected ten AMD ISA XML files, found ${xmlFiles.size}")

    val xml = xmlFiles.flatMap(parseOfficialXML(_)).map(value =>
      value.copy(name = CarryNames.getOrElse(value.name, value.name))
    )
    val official = xml ++ xml.filter(_.isa == "RDNA3_5").map(_.copy(isa = "GFX11_7"))
    // ACO keeps the pre-GFX11 names of opcodes GFX11 renamed; on GFX11.7 the RDNA3.5 encoding of the same opcode names
    // them
    val aco = parseACO(references.resolve("mesa/src/amd/compiler/aco_opcodes.py"), python).map { original =>
      val name = original.name.stripSuffix("_e64")
      if (
        original.isa != "GFX11_7" ||
        official.exists(value => (value.isa == "RDNA3_5" || value.isa == "RDNA4") && value.name == name)
      ) original
      else {
        val format = original.format match {
          case "GLOBAL"  => "FLAT_GLBL"
          case "SCRATCH" => "FLAT_SCRATCH"
          case format    => format
        }
        official.find(value => value.isa == "RDNA3_5" && value.opcode == original.opcode && value.format == format)
          .fold(original)(value => original.copy(name = value.name))
      }
    }
    val older = aco.map { original =>
      val normalisedName = original.name.stripSuffix("_e64")
      val record         = original.copy(
        name = normalisedName match {
          case name
              if original.isa == "GFX11_7" &&
                MinMaxName.matches(name) =>
            FloatSuffix.replaceFirstIn(name, "_num_f")
          case name if CompareName.matches(name) =>
            name.replace("_lg_", "_ne_").replace("_tru_", "_t_")
          case "v_addc_co_u32"    => "v_add_co_ci_u32"
          case "v_subb_co_u32"    => "v_sub_co_ci_u32"
          case "v_subbrev_co_u32" => "v_subrev_co_ci_u32"
          case name               => name
        },
        format = original.format match {
          case "GLOBAL"  => "FLAT_GLBL"
          case "SCRATCH" => "FLAT_SCRATCH"
          case format    => format
        }
      )
      val parents    = if (record.isa == "GFX11_7") Gfx117Parents else PreGfx11Parents
      val compatible = official.filter(value =>
        parents(value.isa) &&
          value.name == record.name &&
          (value.format == record.format || value.format == s"${record.format}_INST_LITERAL" ||
            (record.format == "VOP3" && value.format == "VOP3_SDST_ENC"))
      )
        .sortBy(value => (if (value.isa == "CDNA1") 0 else if (value.isa == "RDNA1") 1 else 2, value.isa))
      val operands = compatible.headOption.orElse {
        Option.when(record.format == "VOP2") {
          official.find(value => value.name == record.name && value.isa == "CDNA1" && value.format == "VOP3")
            .map(value =>
              value.copy(
                format = "VOP2",
                operands = value.operands.map { operand =>
                  if (operand.field == "SRC1") operand.copy(field = "VSRC1") else operand
                }
              )
            )
        }.flatten.orElse(
          // the reverse subtractions GFX8 dropped take the operands of the subtractions
          Option.when(ReverseSubtractName.matches(record.name))(
            official.find(value =>
              value.name == record.name.replace("_rsub", "_sub") && value.isa == "CDNA1" &&
                value.format == record.format
            )
          ).flatten
        )
      }
      if (DocumentedFormats(record.format))
        operands.fold {
          if (record.format == "DS" && record.name.contains("_src2_"))
            record.copy(operands = Vector(Operand("ADDR", "VGPR", true, false, false, 32)))
          else if (FallbackFormats(record.format)) {
            val args   = record.operands.filterNot(_.implicitOperand)
            val fields = record.format match {
              case "VOP1" => Vector(if (record.name == "v_readfirstlane_b32") "SDST" else "VDST", "SRC0")
              case "VOP2" => Vector("VDST", "SRC0", "VSRC1")
              case "VOP3" => Vector("VDST", "SRC0", "SRC1", "SRC2")
              case "VOPC" => Vector("SRC0", "VSRC1")
              case _      => Vector("VDST", "VSRC", "ATTR")
            }
            val mapped = args.zip(fields).map { case (operand, field) =>
              operand.copy(
                field = field,
                kind = if (field == "SDST") "SDST"
                else if (field.startsWith("SRC")) "SRC" else if (field == "ATTR") "ATTR" else "VGPR"
              )
            }
            record.copy(operands =
              if (record.format == "VOPC") Operand("", "VCC", false, true, false, 64) +: mapped else mapped
            )
          } else record
        }(value => record.copy(format = value.format, operands = value.operands))
      else record
    }
    // opcodes GFX9 and later dropped, whose VOP3 forms no document lists, by an operation whose operands they share
    val extended = older.filter(record => record.isa.startsWith("GFX") && record.isa != "GFX11_7").flatMap { record =>
      val opcode = record.format match {
        case "VOP1" => Some(record.opcode + (if (record.isa == "GFX6" || record.isa == "GFX7") 0x180 else 0x140))
        case "VOP2" => Some(record.opcode + 0x100)
        case "VOPC" => Some(record.opcode)
        case "VINTRP" if PartialFunction.cond(record.isa) { case "GFX8" | "GFX9" => true } =>
          Some(record.opcode + 0x270)
        case _ => None
      }
      opcode.toVector.flatMap { value =>
        official.find(entry =>
          entry.isa == "CDNA1" && entry.name == record.name && Vop3Formats(entry.format)
        )
          .orElse(official.find(entry => entry.isa == "RDNA1" && entry.name == record.name && entry.format == "VOP3"))
          .orElse(RetiredOperations.get(record.name).flatMap(name =>
            official.find(entry => entry.isa == "CDNA1" && entry.name == name && entry.format == "VOP3")
          ))
          .map(entry => record.copy(format = entry.format, opcode = value, operands = entry.operands))
      }
    }
    val subwords = older.flatMap { record =>
      if (PartialFunction.cond(record.isa) { case "GFX8" | "GFX9" => true } && VopFormats(record.format))
        official.view.filter(value =>
          value.isa == "CDNA1" && value.name == record.name && value.format.startsWith(s"${record.format}_VOP_")
        )
          .map(value => record.copy(format = value.format, operands = value.operands))
      else Vector.empty
    }
    val nsa = older.filter(entry => entry.isa == "RDNA1" && entry.name.startsWith("image_bvh")).flatMap { entry =>
      official.collect {
        case value if value.isa == "RDNA2" && value.name == entry.name && value.format.startsWith("MIMG_NSA") =>
          entry.copy(format = value.format, operands = value.operands)
      }
    }
    val gfx1170 = older.filter(_.isa == "GFX11_7").flatMap { record =>
      val base     = if (record.format == "VOPD") "VOPDXY" else record.format
      val promoted = base match {
        case "VOP1" => Some(record.opcode + 0x180)
        case "VOP2" => Some(record.opcode + 0x100)
        case "VOPC" => Some(record.opcode)
        case _      => None
      }
      official.view.filter(value => value.isa == "RDNA4" && value.name == record.name).flatMap { value =>
        val format = value.format.takeWhile(_ != '_')
        val opcode = if (format == base) Some(record.opcode)
        else if (format == "VOP3") promoted
        else None
        opcode.map(opcode => value.copy(isa = "GFX11_7", opcode = opcode))
      }
    }
    val predecessors = older ++ gfx1170
    val inherited    = official.filterNot(value =>
      value.isa == "GFX11_7" && predecessors.exists(record =>
        record.isa == value.isa && record.format == value.format && record.opcode == value.opcode
      )
    )
    val additions = (older ++ extended ++ subwords ++ nsa).filterNot(value =>
      (value.isa == "RDNA1" && official.exists(record =>
        record.isa == value.isa && record.format == value.format &&
          (record.opcode == value.opcode || record.name == value.name)
      )) ||
        (value.isa == "GFX11_7" && gfx1170.exists(record => record.name == value.name && record.format == value.format))
    )
    val historical = (additions ++ inherited).filter(entry => LegacyGenerationSet(entry.isa)).flatMap {
      entry =>
        SignedCarryNames.get(entry.name).filter(_ => EarliestGenerations(entry.isa) || entry.name.contains("_ci_")).map(
          name =>
            entry.copy(name = name)
        )
    }
    val carryAliases = (additions ++ inherited).flatMap { entry =>
      CarryAliasNames.get(entry.name)
        .map(name => entry.copy(name = name))
    }
    val legacyNames = (additions ++ inherited).flatMap { entry =>
      if (entry.isa == "GFX8") Gfx8Names.get(entry.name).map(name => entry.copy(name = name))
      else if (entry.isa == "GFX9" && entry.name.startsWith("v_fma_mix"))
        Some(entry.copy(name = entry.name.replace("v_fma_", "v_mad_")))
      else None
    }
    val supplement = parseLLVM(references.resolve("llvm/llvm/lib/Target/AMDGPU"), additions ++ inherited ++ gfx1170)
    // CDNA's mixed-precision multiply-adds fuse, as LLVM spells them, though AMD's documents name them MAD
    val records =
      (additions ++ inherited ++ historical ++ carryAliases ++ legacyNames ++ supplement ++ gfx1170).map(record =>
        if (CdnaGenerationSet(record.isa) && record.name.startsWith("v_mad_mix"))
          record.copy(name = record.name.replace("v_mad_", "v_fma_"))
        else record
      ).distinct.sortBy(record => (record.name, record.isa, record.format, record.opcode))
    val layouts = xmlFiles.flatMap(parseLayouts(_)).flatMap { layout =>
      val format = layout("format").str
      if (format == "VOP3" || format.startsWith("VOP3_VOP_DPP") || format.startsWith("VOP3_INST_LITERAL")) {
        val copy = ujson.read(ujson.write(layout))
        copy("format") = format.replace("VOP3", "VOP3_BITOP3")
        copy("fields") =
          ujson.Arr.from(copy("fields").arr.filterNot(field => BitOp3Dropped(field("name").str)) :+
            ujson.Obj(
              "name"   -> "BITOP3",
              "ranges" -> ujson.Arr(
                ujson.Obj("offset" -> 61, "bits" -> 3, "padding" -> 0),
                ujson.Obj("offset" -> 8, "bits"  -> 3, "padding" -> 0),
                ujson.Obj("offset" -> 59, "bits" -> 2, "padding" -> 0)
              )
            ))
        val byteSelect = ujson.read(ujson.write(layout))
        byteSelect("format") = format.replace("VOP3", "VOP3_BYTESEL")
        byteSelect("fields") = ujson.Arr.from(byteSelect("fields").arr.map { field =>
          if (OpSelFields(field("name").str)) ujson.Obj(
            "name" -> "BYTE_SEL",
            // LLVM VOP3 source byte selection reverses these two bits.
            "ranges" -> ujson.Arr(
              ujson.Obj("offset" -> 12, "bits" -> 1, "padding" -> 0),
              ujson.Obj("offset" -> 11, "bits" -> 1, "padding" -> 0)
            )
          )
          else field
        })
        Vector(layout, copy, byteSelect)
      } else Vector(layout)
    }.map { layout =>
      if (layout("isa").str == "CDNA5" && ScaledFormats(layout("format").str)) {
        val scale = records.find(value =>
          value.isa == "CDNA5" && value.name ==
            (if (layout("format").str == "VOP3PX2") "v_wmma_ld_scale_paired_b32" else "v_wmma_ld_scale16_paired_b64")
        )
          .getOrElse(throw IllegalArgumentException("missing WMMA scale instruction"))
        val base = BigInt("cc000000", 16)
        layout("mask") = ((BigInt("ff000000", 16) << 64) | BigInt("ffff0000", 16)).toString(16)
        layout("identifiers") = ujson.Arr(((base << 64) | base | (BigInt(scale.opcode) << 16)).toString(16))
        // The fourth OPSEL_HI bit is matrix_b_reuse in the scale word.
        layout("fields").arr.find(_("name").str == "OPSEL_HI").foreach { field =>
          field("ranges") = ujson.Arr.from(field("ranges").arr.filter(_("offset").num != 14))
        }
        layout
      } else layout
    }
    val hwregText =
      Files.readString(references.resolve("llvm/llvm/lib/Target/AMDGPU/SIDefines.h"), StandardCharsets.UTF_8)
    val hwregIds = HwregId.findAllMatchIn(hwregText).map(value =>
      s"HW_REG_WAVE_${value.group(1)}" -> ujson.Str(value.group(2))
    ).toVector
    val operandLayouts = xmlFiles.flatMap(parseOperandLayouts(_)).map { layout =>
      if (HwregIsas(layout("isa").str) && layout("kind").str == "HWREG")
        layout("fields").arr.filter(_("field")("name").str == "ID").foreach(field =>
          hwregIds.foreach { case (name, value) => field("names")(name) = value }
        )
      layout
    }
    def extendLayouts(values: Vector[ujson.Value]): Vector[ujson.Value] = values ++
      values.filter(_("isa").str == "RDNA3_5").map { value =>
        ujson.Obj.from(value.obj.toVector.filterNot(_._1 == "isa") :+ ("isa" -> ujson.Str("GFX11_7")))
      }
    val illegalSource =
      Files.readString(references.resolve("llvm/llvm/lib/Target/AMDGPU/SIInstructions.td"), StandardCharsets.UTF_8)
    val illegalWord = IllegalWord.findFirstMatchIn(
      illegalSource
    ).map(_.group(1)).getOrElse(throw IllegalArgumentException("missing V_ILLEGAL encoding"))
    val illegalLayouts = records.filter(_.format == "ILLEGAL").map(value =>
      ujson.Obj(
        "isa"         -> value.isa,
        "format"      -> "ILLEGAL",
        "bits"        -> 32,
        "mask"        -> "ffffffff",
        "identifiers" -> ujson.Arr(illegalWord),
        "fields"      -> ujson.Arr()
      )
    )
    Extracted(
      records,
      normaliseLayouts(extendLayouts(layouts) ++ illegalLayouts, references),
      normaliseOperandLayouts(extendLayouts(operandLayouts), references) ++
        parseBufferFormats(references.resolve("llvm/llvm/lib/Target/AMDGPU/SIDefines.h"))
    )
  }

  private def parseLLVM(root: Path, existing: Vector[Instruction]): Vector[Instruction] = {
    def records(file: String, family: String): Vector[(String, Int)] = {
      val text    = Files.readString(root.resolve(file), StandardCharsets.UTF_8)
      val Pattern = (raw"defm\s+([A-Z0-9_]+)(?:_w32)?\s*:\s*" + family + raw"\s*<0x([0-9a-fA-F]+)").r
      Pattern.findAllMatchIn(text).map(value =>
        value.group(1).toLowerCaseRoot -> Integer.parseInt(value.group(2), 16)
      ).toVector
    }
    def arg(field: String, kind: String, bits: Int): Operand =
      Operand(field, kind, !field.startsWith("VDST"), field.startsWith("VDST"), implicitOperand = false, bits)
    val compares = records("VOPCInstructions.td", "VOPCX?_Real_gfx6_gfx7").filter(_._1.startsWith("v_cmps")).flatMap {
      case (name, opcode) =>
        val bits = if (name.endsWith("f64")) 64 else 32
        Vector("GFX6", "GFX7").flatMap { isa =>
          Vector(
            Instruction(
              name,
              isa,
              "VOPC",
              opcode,
              Vector(arg("", "VCC", 64), arg("SRC0", "SRC", bits), arg("VSRC1", "VGPR", bits))
            ),
            Instruction(
              name,
              isa,
              "VOP3",
              opcode,
              Vector(arg("VDST", "SREG", 64), arg("SRC0", "SRC", bits), arg("SRC1", "SRC", bits))
            )
          )
        }
    }
    val legacyVop2 = records("VOP2Instructions.td", "VOP2_Real_gfx6_gfx7").flatMap { case (name, opcode) =>
      Vector("GFX6", "GFX7").flatMap { isa =>
        existing.find(entry => entry.name == name && entry.isa == isa && entry.format == "VOP3").map { entry =>
          entry.copy(
            format = "VOP2",
            opcode = opcode,
            operands = entry.operands.map { operand =>
              if (operand.field == "SRC1") operand.copy(field = "VSRC1", kind = "VGPR") else operand
            }
          )
        }
      }
    }
    val gfx9Fmac = records("VOP2Instructions.td", "VOP2_Real_e32e64_vi").filter(_._1 == "v_fmac_f32")
      .flatMap { case (name, opcode) =>
        existing.view.filter(entry => entry.name == name && entry.isa == "CDNA1" && VopOrVop3(entry.format))
          .map(entry => entry.copy(isa = "GFX9", opcode = if (entry.format == "VOP2") opcode else entry.opcode))
      }
    val packed = records("VOP3PInstructions.td", "VOP3P_Real_gfx1250").filter { case (name, _) =>
      name.startsWith("v_pk_") && (name.endsWith("_f64") || name.endsWith("_u64"))
    }.map { case (name, opcode) =>
      val sources = if (name.contains("fma") || name.contains("lshl_add")) 3 else 2
      Instruction(
        name,
        "CDNA5",
        "VOP3P",
        opcode,
        arg("VDST", "VGPR", 128) +: Vector.tabulate(sources)(index =>
          arg(s"SRC$index", "SRC", if (name.contains("lshl_add") && index == 1) 64 else 128)
        )
      )
    }
    val accumulators = records("VOP3PInstructions.td", "VOP3P_Real_MAI").filter(_._1.startsWith("v_accvgpr_")).flatMap {
      case (name, opcode) =>
        CdnaGenerations.map { isa =>
          val write = name.contains("write")
          Instruction(
            name,
            isa,
            "VOP3P",
            opcode,
            Vector(arg("VDST", if (write) "ACCVGPR" else "VGPR", 32), arg("SRC0", if (write) "SRC" else "ACCVGPR", 32))
          )
        }
    }
    val scale = records("VOP3PInstructions.td", "VOP3P_Real_vi").filter(_._1 == "v_mfma_ld_scale_b32").map {
      case (name, opcode) =>
        Instruction(name, "CDNA4", "VOP3P", opcode, Vector(arg("SRC0", "SRC", 32), arg("SRC1", "SRC", 32)))
    }
    def vectorVariant(entry: Instruction, format: String) = entry.copy(
      format = format,
      operands = entry.operands.map(operand =>
        if (operand.field == "SRC0") operand.copy(field = "VSRC0", kind = "VGPR") else operand
      )
    )
    val subwords = existing.filter(entry =>
      (entry.name == "v_pk_fmac_f16" && CdnaGenerationSet(entry.isa) && entry.format == "VOP2") ||
        (entry.name == "v_rndne_f64" && LaterCdnaGenerations(entry.isa) && entry.format == "VOP1")
    ).map(entry =>
      vectorVariant(entry, entry.format + (if (entry.name == "v_pk_fmac_f16") "_VOP_SDWA" else "_VOP_DPP"))
    )
    val dots = existing.filter(entry =>
      (entry.isa == "RDNA1" && entry.name == "v_dot2c_f32_f16" && entry.format == "VOP2") ||
        (entry.isa == "GFX11_7" && entry.name == "v_dot4_f32_bf8_bf8" && entry.format == "VOP3P")
    ).flatMap { entry =>
      Vector("DPP16", "DPP8").map(suffix => vectorVariant(entry, s"${entry.format}_VOP_$suffix"))
    }
    val signedDots = records("VOP3PInstructions.td", "VOP3P_Real_SignedDot_gfx11").flatMap { case (name, opcode) =>
      val unsigned = name.replace("_i8", "_iu8").replace("_i4", "_iu4")
      existing.view.filter(entry =>
        SignedDotGenerations(entry.isa) && entry.name == unsigned && entry.format == "VOP3P"
      )
        .map(_.copy(name = name, opcode = opcode))
    }
    val reverseAtomic = existing.filter(entry => entry.isa == "GFX6" && entry.name == "image_atomic_rsub").flatMap {
      entry =>
        existing.find(value => value.isa == "GFX6" && value.name == "image_atomic_sub" && value.format == "MIMG")
          .map(value => entry.copy(isa = "GFX7", operands = value.operands))
    }
    val illegal = IllegalGenerations
      .map(isa => Instruction("v_illegal", isa, "ILLEGAL", 0, Vector.empty))
    val legacyHalf =
      (records("VOP3Instructions.td", "VOP3_F16_Real_vi") ++
        records("VOP3Instructions.td", "VOP3Interp_F16_Real_vi")).flatMap { case (name, opcode) =>
        val legacyName =
          name.replace("_f16", "_legacy_f16").replace("_u16", "_legacy_u16").replace("_i16", "_legacy_i16")
        existing.find(entry => entry.name == legacyName && entry.isa == "CDNA1" && entry.format == "VOP3")
          .map(_.copy(name = name, isa = "GFX8", opcode = opcode))
      }
    val legacyBuffer = records("BUFInstructions.td", "MUBUF_Real_gfx6").filter(_._1 == "buffer_wbinvl1_sc")
      .map { case (name, opcode) => Instruction(name, "GFX6", "MUBUF", opcode, Vector.empty) }
    val bufferStores = records("BUFInstructions.td", "MUBUF_Real_AllAddr_gfx6_gfx7_gfx10_Renamed_gfx13")
      .filter(_._1 == "buffer_store_dwordx3").flatMap { case (name, opcode) =>
        existing.find(entry => entry.name == name && entry.isa == "GFX7" && entry.format == "MUBUF").map(_.copy(
          isa = "GFX6",
          opcode = opcode
        ))
      }
    val barriers = records("SOPInstructions.td", "SOP1_IMM_Real_gfx12_gfx13").filter { case (name, _) =>
      BarrierImmediates(name)
    }.flatMap { case (name, opcode) =>
      Vector("RDNA4", "CDNA5").map { isa =>
        Instruction(
          name.stripSuffix("_imm"),
          isa,
          "SOP1",
          opcode,
          (if (name.startsWith("s_get")) Vector(arg("SDST", "SREG", 32)) else Vector.empty) :+ arg("SSRC0", "SRC", 32)
        )
      }
    }
    val barrierLeave = records("SOPInstructions.td", "SOPP_Real_32_gfx12").filter(_._1 == "s_barrier_leave").flatMap {
      case (name, opcode) =>
        Vector("RDNA4", "CDNA5").map(isa => Instruction(name, isa, "SOPP", opcode, Vector.empty))
    }
    val wmmaScale =
      records("VOP3PInstructions.td", "VOP3P_Real_gfx1250").filter(_._1.startsWith("v_wmma_ld_scale")).map {
        case (name, opcode) =>
          val bits = if (name.endsWith("b64")) 64 else 32
          Instruction(name, "CDNA5", "VOP3P", opcode, Vector(arg("SRC0", "SRC", bits), arg("SRC1", "SRC", bits)))
      }
    val wmmaDouble = records("VOP3PInstructions.td", "VOP3P_Real_WMMA_gfx1250").filter(_._1 ==
      "v_wmma_f64_16x16x4_f64").map { case (name, opcode) =>
      Instruction(
        name,
        "CDNA5",
        "VOP3P",
        opcode,
        Vector(arg("VDST", "VGPR", 512), arg("SRC0", "SRC", 128), arg("SRC1", "SRC", 128), arg("SRC2", "SRC", 512))
      )
    }
    val tensor = records("FLATInstructions.td", "VFLAT_Real_gfx1250").filter(_._1.startsWith("tensor_")).map {
      case (name, opcode) =>
        Instruction(
          name,
          "CDNA5",
          "VGLOBAL",
          opcode,
          if (name == "tensor_save") Vector(arg("SADDR", "SREG", 64)) else Vector.empty
        )
    }
    (compares ++ legacyVop2 ++ gfx9Fmac ++ packed ++ accumulators ++ scale ++ wmmaScale ++ wmmaDouble ++ subwords ++ dots ++
      signedDots ++ reverseAtomic ++ illegal ++ legacyHalf ++ legacyBuffer ++ bufferStores ++ barriers ++
      barrierLeave ++ tensor).filterNot(value =>
      existing.exists(entry => entry.name == value.name && entry.isa == value.isa && entry.format == value.format)
    )
  }

  private def xmlDocument(input: Path): org.w3c.dom.Document = {
    val factory = DocumentBuilderFactory.newInstance()
    factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
    factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
    factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
    factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "")
    factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
    factory.setXIncludeAware(false)
    factory.setExpandEntityReferences(false)
    factory.setNamespaceAware(false)
    factory.newDocumentBuilder().parse(input.toFile)
  }

  private object Layout {
    case class Field(name: String, ranges: Vector[BitRange]) derives ReadWriter
    case class Condition(field: String, values: Vector[Int], included: Boolean) derives ReadWriter
  }
  private case class Layout(
      isa: String,
      format: String,
      bits: Int,
      mask: String,
      identifiers: Vector[String],
      fields: Vector[Layout.Field],
      conditions: Vector[Vector[Layout.Condition]] = Vector(Vector.empty)
  ) derives ReadWriter

  private val HexLiteral = "0x[0-9a-fA-F]+".r

  private val Assignment = raw"let Inst\{([0-9]+)(?:-([0-9]+))?\}\s*=\s*([^;]+);".r

  private def llvmClass(text: String, pattern: String, owner: String): String =
    pattern.r.findFirstIn(text).getOrElse(throw IllegalArgumentException(s"missing LLVM class $owner"))

  private def normaliseLayouts(values: Vector[ujson.Value], references: Path): Vector[ujson.Value] = {
    val root = references.resolve("llvm/llvm/lib/Target/AMDGPU")
    def assignments(file: String, owner: String): Vector[(BitRange, String)] = {
      val text = Files.readString(root.resolve(file), StandardCharsets.UTF_8)
      val body = llvmClass(text, raw"(?ms)^(?:multi)?class\s+" + owner + raw"(?=[<\s]).*?^}", owner)
      Assignment.findAllMatchIn(body).map { value =>
        val high = value.group(1).toInt
        val low  = Option(value.group(2)).fold(high)(_.toInt)
        BitRange(low.unsafe, (high - low + 1).unsafe, 0) -> value.group(3).trim
      }.toVector
    }
    val vop3  = assignments("VOPInstructions.td", "VOP3a") ++ assignments("VOPInstructions.td", "VOP3a_gfx6_gfx7")
    val ds    = assignments("DSInstructions.td", "Base_DS_Real_gfx6_gfx7_gfx10_gfx11_gfx12_gfx13")
    val mubuf = assignments("BUFInstructions.td", "Base_MUBUF_Real_gfx6_gfx7_gfx10") ++
      assignments("BUFInstructions.td", "MUBUF_Real_gfx6_gfx7")
    val mtbuf = assignments("BUFInstructions.td", "Base_MTBUF_Real_gfx6_gfx7_gfx10_gfx11") ++
      assignments("BUFInstructions.td", "Base_MTBUF_Real_gfx6_gfx7_gfx10")
    val interpolation = assignments("SIInstrFormats.td", "VINTRPe")
    val exportLayout  = assignments("SIInstrFormats.td", "EXPe")
    val smrd          = assignments("SMInstructions.td", "SMRD_Real_si")
    def llvmField(values: Vector[(BitRange, String)], name: String, symbol: String): Layout.Field = {
      val matches = values.filter { case (_, expression) =>
        if (symbol.startsWith("ps.")) expression == symbol
        else (raw"\b" + java.util.regex.Pattern.quote(symbol) + raw"\b").r.findFirstIn(expression).nonEmpty
      }
      require(matches.size == 1, s"ambiguous or missing LLVM field $symbol")
      val (range, expression) = matches.head
      val padding             = (java.util.regex.Pattern.quote(symbol) + raw"\{[0-9]+-([0-9]+)\}").r
        .findFirstMatchIn(expression).map(_.group(1).toInt).getOrElse(0)
      Layout.Field(name, Vector(range.copy(padding = padding.unsafe)))
    }
    def renamed(
        fields: Vector[Layout.Field],
        values: Vector[(BitRange, String)],
        renames: (String, (String, String))*
    ): Vector[Layout.Field] = fields.map(field =>
      renames.collectFirst { case (from, (name, symbol)) if field.name == from => llvmField(values, name, symbol) }
        .getOrElse(field)
    )
    def prefix(layout: Layout, values: Vector[(BitRange, String)]): Layout = {
      val (range, value) = values.filter(value => HexLiteral.matches(value._2))
        .maxByOption(_._1.offset).getOrElse(throw IllegalArgumentException(s"missing LLVM ${layout.format} prefix"))
      layout.copy(
        mask = (((BigInt(1) << range.bits) - 1) << range.offset).toString(16),
        identifiers = Vector((BigInt(value.drop(2), 16) << range.offset).toString(16))
      )
    }

    val dual     = assignments("VOPDInstructions.td", "VOPDe")
    val official = values.map(read[Layout](_)).map(value =>
      if (value.format == "VOPDXY_X") value.copy(format = "VOPDXY") else value
    ).map(value =>
      value.copy(fields = value.fields.map {
        case field if field.name == "ATTRCHAN"     => field.copy(name = "ATTR_CHAN")
        case field if field.name == "IOFFSET"      => field.copy(name = "OFFSET")
        case field if field.name.contains("OPSEL") => field.copy(name = field.name.replace("OPSEL", "OP_SEL"))
        case field                                 => field
      })
    ).map { layout =>
      if (layout.format == "VOP3PX2" && layout.isa == "CDNA4") {
        // LLVM emits the scale-prefix word followed by the MFMA word; AMD's identifiers describe the latter.
        val first  = layout.identifiers.map(BigInt(_, 16)).min - (BigInt(1) << 16)
        val prefix = (first & BigInt("ff800000", 16)) << 64
        layout.copy(
          mask = ((BigInt("ff800000", 16) << 64) | BigInt("ffff0000", 16)).toString(16),
          identifiers = Vector((prefix | first).toString(16))
        )
      } else if (layout.format == "VOPDXY") prefix(layout, dual)
      else layout
    }
    // ACO's GFX6-9 encoders share these layouts with CDNA1. GFX6/7 VOP3 moves OP and CLAMP.
    val legacy = official.filter(_.isa == "CDNA1").flatMap { layout =>
      lazy val vop3Fields = layout.fields.collect {
        case field if field.name == "OP"     => llvmField(vop3, "OP", "op")
        case field if field.name == "CLAMP"  => llvmField(vop3, "CLAMP", "clamp")
        case field if field.name != "OP_SEL" => field
      }
      lazy val mubufFields =
        renamed(layout.fields, mubuf, "LDS" -> ("ADDR64", "addr64"), "SLC" -> ("SLC", "CPolBit.SLC"))
      lazy val dsFields    = renamed(layout.fields, ds, "OP" -> ("OP", "op"), "GDS" -> ("GDS", "gds"))
      lazy val mtbufFields = renamed(layout.fields, mtbuf, "OP" -> ("OP", "op"))
      LegacyGenerations.map { isa =>
        val early = isa == "GFX6" || isa == "GFX7"
        layout.format match {
          case "VINTRP" if early                            => prefix(layout.copy(isa = isa), interpolation)
          case format if early && format.startsWith("VOP3") =>
            prefix(layout.copy(isa = isa, fields = vop3Fields), vop3)
          case "MUBUF" if early || isa == "GFX8"           => layout.copy(isa = isa, fields = mubufFields)
          case format if early && DataShareFormats(format) =>
            prefix(layout.copy(isa = isa, fields = dsFields), ds)
          case "MTBUF" if early => prefix(layout.copy(isa = isa, fields = mtbufFields), mtbuf)
          case "EXP" if early   => prefix(layout.copy(isa = isa), exportLayout)
          case "SMEM" if early  =>
            prefix(
              layout.copy(
                isa = isa,
                bits = 32,
                fields = Vector(
                  llvmField(smrd, "OP", "op"),
                  llvmField(smrd, "SDATA", "sdst"),
                  llvmField(smrd, "SBASE", "sbase"),
                  llvmField(smrd, "IMM", "ps.has_offset"),
                  llvmField(smrd, "OFFSET", "offset")
                )
              ),
              smrd
            )
          case _ => layout.copy(isa = isa)
        }
      }
    }
    (official ++ legacy).map(writeJs(_))
  }

  private def encodingCondition(expression: Element): Vector[Layout.Condition] = {
    def children(value: Element): Vector[Element] =
      Vector.tabulate(value.getChildNodes.getLength)(value.getChildNodes.item(_)).collect {
        case element: Element if element.getTagName != "ty" => element
      }
    val parts = children(expression)
    expression.getTagName match {
      case "lit" if expression.getAttribute("val") == "1" => Vector.empty
      case "op"                                           => expression.getAttribute("type") match {
          case "&&"                                   => parts.flatMap(encodingCondition(_))
          case "==" | "!=" | ".within" | ".notwithin" =>
            require(
              parts.size == 2 && parts.head.getAttribute("type") == ".fieldderef",
              "unsupported encoding comparison"
            )
            val field = directChildren(parts.head, "id").map(_.getAttribute("val"))
            require(field.size == 1, "ambiguous encoding field")
            val right  = parts(1)
            val values = if (right.getTagName == "lit") Vector(right.getAttribute("val").toInt)
            else {
              require(right.getAttribute("type") == ".cons_array", "unsupported encoding values")
              children(right).map { value =>
                require(value.getTagName == "lit", "nonliteral encoding value")
                value.getAttribute("val").toInt
              }
            }
            Vector(Layout.Condition(field.head, values, Set("==", ".within")(expression.getAttribute("type"))))
          case other => throw IllegalArgumentException(s"unsupported encoding condition $other")
        }
      case other => throw IllegalArgumentException(s"unsupported encoding expression $other")
    }
  }

  private def parseLayouts(input: Path): Vector[ujson.Value] = {
    val document = xmlDocument(input)
    val isa      = input.getFileName.toString.stripPrefix("amdgpu_isa_").stripSuffix(".xml").toUpperCaseRoot
    val nodes    = document.getElementsByTagName("Encoding")
    Vector.tabulate(nodes.getLength) { index =>
      val encoding = element(nodes.item(index), "Encoding")
      val fields   =
        directChildren(requiredDirect(requiredDirect(encoding, "MicrocodeFormat"), "BitMap"), "Field").map { field =>
          ujson.Obj("name" -> requiredText(field, "FieldName"), "ranges" -> ujson.Arr.from(bitRanges(field)))
        }
      def number(element: Element): String =
        BigInt(element.getTextContent.trim, element.getAttribute("Radix").toInt).toString(16)
      ujson.Obj(
        "isa"         -> isa,
        "format"      -> requiredText(encoding, "EncodingName").stripPrefix("ENC_"),
        "bits"        -> requiredText(encoding, "BitCount").toInt,
        "mask"        -> number(requiredDirect(encoding, "EncodingIdentifierMask")),
        "identifiers" ->
          ujson.Arr.from(directChildren(requiredDirect(encoding, "EncodingIdentifiers"), "EncodingIdentifier").map(e =>
            ujson.Str(number(e))
          )),
        "fields"     -> ujson.Arr.from(fields),
        "conditions" -> writeJs(directChildren(
          requiredDirect(encoding, "EncodingConditions"),
          "EncodingCondition"
        ).map { condition =>
          val expression = requiredDirect(condition, "CondtionExpression")
          val children = Vector.tabulate(expression.getChildNodes.getLength)(expression.getChildNodes.item(_)).collect {
            case e: Element => e
          }
          require(children.size == 1, "encoding condition must have one expression")
          encodingCondition(children.head)
        })
      )
    }
  }

  private val ShiftOperator = java.util.regex.Pattern.compile("<<")

  private val Entry = raw"([A-Za-z_][A-Za-z_0-9]*)(?:\s*=\s*(.+))?".r

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def enumValues(text: String, namespace: String, name: String): Vector[(String, Int)] = {
    val start = text.indexOf(s"namespace $namespace {")
    require(start >= 0, s"missing LLVM namespace $namespace")
    val scope       = text.substring(start).split(s"} // namespace $namespace", 2).head
    val declaration =
      (raw"enum\s*" + java.util.regex.Pattern.quote(name) + raw"\s*(?::\s*[A-Za-z_][A-Za-z_0-9]*)?\s*\{").r
    val body = declaration.findFirstMatchIn(scope).map(value => scope.substring(value.end).takeWhile(_ != '}'))
      .getOrElse(throw IllegalArgumentException(s"missing LLVM enum $namespace::$name"))
    body.replaceAll("//[^\n]*", "").split(',').toVector.map(_.trim).filter(_.nonEmpty)
      .foldLeft((0, Vector.empty[(String, Int)])) { case ((next, values), member) =>
        val (key, value) = member match {
          case Entry(key, raw) =>
            def atom(text: String): Int = parseIntLiteral(text.trim)
              .orElse(values.find(_._1 == text.trim).map(_._2))
              .getOrElse(throw IllegalArgumentException(s"unsupported LLVM enum initialiser: $member"))
            def expression(text: String): Int = text.split("\\|", -1).map { term =>
              ShiftOperator.split(term, -1).map(_.trim).toVector match {
                case Vector(value)        => atom(value)
                case Vector(value, shift) =>
                  val bits = atom(shift)
                  require(bits >= 0 && bits < 32, s"invalid LLVM enum shift: $member")
                  atom(value) << bits
                case _ => throw IllegalArgumentException(s"unsupported LLVM enum initialiser: $member")
              }
            }.foldLeft(0)(_ | _)
            key -> Option(raw).fold(next)(expression(_))
          case _ => throw IllegalArgumentException(s"unsupported LLVM enum member: $member")
        }
        (value + 1, values :+ (key -> value))
      }._2
  }

  private def renderOperandConstants(text: String): String = {
    val groups = OperandConstantEnums.map { case (namespace, name, owner) =>
      val values    = enumValues(text, namespace, name)
      val constants = values.map { case (key, value) => s"    val $key: Int = $value" }.mkString("\n")
      s"  object $owner {\n$constants\n  }\n"
    }.mkString
    def names(name: String, member: String): String = {
      val values = enumValues(text, "SDWA", name).sortBy(_._2)
      require(values.map(_._2) == values.indices.toVector, s"noncontiguous LLVM SDWA $name")
      s"  val $member: Vector[NonEmpty[String]] = Vector(${values.map(value => ujson.write(value._1)).mkString(", ")})\n"
    }
    val policy                                                      = enumValues(text, "CPol", "CPol").toMap
    def table(name: String, entries: Vector[(String, Int)]): String =
      s"  val $name: Map[String, Int] = Map(${entries.sortBy(_._1).map { case (key, value) =>
          s"${ujson.write(key)} -> $value"
        }.mkString(", ")})\n"
    val hints = policy.toVector.collect {
      case (name, value)
          if name.startsWith("TH_") && name != "TH_RESERVED" &&
            !name.startsWith("TH_ATOMIC_") && !name.startsWith("TH_TYPE_") && !name.startsWith("TH_REAL_") =>
        name.stripPrefix("TH_") -> value
    }
    val scopes =
      ScopeNames.map(name => name -> (policy(s"SCOPE_$name") >>> policy("SCOPE_SHIFT")))
    val atomic = Vector("RT" -> 0, "NT" -> policy("TH_ATOMIC_NT"), "CASCADE" -> policy("TH_ATOMIC_CASCADE"))
    groups + names("SdwaSel", "SdwaSelectors") + names("DstUnused", "SdwaUnused") +
      table("TemporalHints", hints) + table("MemoryScopes", scopes) +
      table(
        "AtomicHints",
        atomic ++ atomic.map { case (name, value) =>
          (if (name == "RT") "RETURN" else s"${name}_RETURN") -> (value | policy("TH_ATOMIC_RETURN"))
        }
      )
  }

  private val SentinelWord = raw"let Inst\{8-0\}\s*=\s*(0x[0-9a-fA-F]+);".r

  private def renderSharedConstants(result: Extracted, references: Path): String = {
    val source = result.operands.find(value => value("isa").str == "CDNA1" && value("kind").str == "SRC")
      .getOrElse(throw IllegalArgumentException("missing CDNA1 source selectors"))("values")
    val registers = FixedRegisters
      .map { case (name, key) => s"    inline val $name = ${source(key).str}" }.mkString("\n")
    val text =
      Files.readString(references.resolve("llvm/llvm/lib/Target/AMDGPU/VOP1Instructions.td"), StandardCharsets.UTF_8)
    def sentinel(name: String, owner: String): String = {
      val body  = llvmClass(text, raw"(?s)class\s+" + owner + raw"\s*<.*?\n}", owner)
      val value = SentinelWord.findFirstMatchIn(body)
        .flatMap(value => parseIntLiteral(value.group(1)))
        .getOrElse(throw IllegalArgumentException(s"missing LLVM $owner source sentinel"))
      s"    inline val $name = $value\n"
    }
    val floating = source.obj.toVector.filter(_._1.contains('.')).sortBy(_._2.str.toInt)
    val numbers  = floating.map(_._1).mkString(", ")
    val bits     = floating.map(value =>
      s"${java.lang.Float.floatToRawIntBits(value._1.toFloat).toLong & 0xffffffffL}L"
    ).mkString(", ")
    s"  object Fixed {\n$registers\n" + sentinel("DppSource", "VOP1_DPP") + sentinel(
      "SdwaSource",
      "VOP1_SDWAe"
    ) +
      s"    val InlineFloatingValues: Vector[Double] = Vector($numbers)\n" +
      s"    val InlineFloatingConstants: Vector[Long] = Vector($bits)\n  }\n"
  }

  private def parseBufferFormats(input: Path): Vector[ujson.Value] = {
    val text = Files.readString(input, StandardCharsets.UTF_8)
    def enumeration(namespace: String, name: String, prefix: String): Vector[(String, ujson.Value)] =
      enumValues(text, namespace, name).takeWhile { case (key, _) => !Set(s"${prefix}MIN", s"${prefix}FIRST")(key) }
        .map { case (key, value) => key.stripPrefix(prefix) -> ujson.Str(value.toString) }
    val buffers = BufferFormatIsas.map { isa =>
      val fields = if (isa.startsWith("RDNA") || isa == "GFX11_7" || isa == "CDNA5") Vector((
        "FORMAT",
        7,
        enumeration(if (GfxTenUnifiedIsas(isa)) "UfmtGFX10" else "UfmtGFX11", "UnifiedFormat", "UFMT_")
      ))
      else Vector(
        ("DFMT", 4, enumeration("MTBUFFormat", "DataFormat", "DFMT_")),
        ("NFMT", 3, enumeration("MTBUFFormat", "NumFormat", "NFMT_"))
      )
      ujson.Obj(
        "isa"    -> isa,
        "kind"   -> "BUFFER_FORMAT",
        "fields" -> ujson.Arr.from(fields.map { case (name, bits, names) =>
          ujson.Obj(
            "field" -> lowBitsField(name, bits),
            "names" -> ujson.Obj.from(names)
          )
        })
      )
    }
    val matrix = MatrixEnums.map { case (field, name, prefix, bits) =>
      ujson.Obj(
        "field" -> lowBitsField(field, bits),
        "names" -> ujson.Obj.from(enumeration("WMMA", name, prefix))
      )
    }
    buffers :+ ujson.Obj("isa" -> "CDNA5", "kind" -> "MATRIX", "fields" -> ujson.Arr.from(matrix))
  }

  private val Flat = """defm\s+(FLAT_SCR_(?:LO|HI))\s*:\s*FLAT_SCR_LOHI_m<"[^"]*",\s*([0-9]+),\s*([0-9]+)>""".r

  private val Register = """defm\s+([A-Z_0-9a-z]+)\s*:\s*SIRegLoHi16\s*<\s*"([^"]*)",\s*([0-9]+)>""".r

  private def normaliseOperandLayouts(values: Vector[ujson.Value], references: Path): Vector[ujson.Value] = {
    val root      = references.resolve("llvm/llvm/lib/Target/AMDGPU")
    val registers = Files.readString(root.resolve("SIRegisterInfo.td"), StandardCharsets.UTF_8)
    val codes     = Register.findAllMatchIn(registers).map(value => value.group(1) -> value.group(3)).toMap
    val flat = Flat.findAllMatchIn(registers).map(value => value.group(1) -> (value.group(2), value.group(3))).toMap
    val constants =
      enumValues(Files.readString(root.resolve("SIDefines.h"), StandardCharsets.UTF_8), "EncValues", "").toMap
    def copy(value: ujson.Value, isa: String): ujson.Value = {
      val result = ujson.read(ujson.write(value))
      result("isa") = isa
      result
    }
    val legacy = values.filter(_("isa").str == "CDNA1").flatMap(value =>
      LegacyGenerations.map(copy(value, _))
    )
    val waitEvent = values.filter(value => value("isa").str == "RDNA3_5" && value("kind").str == "WAIT_EVENT")
      .map(copy(_, "RDNA3"))
    val combined = values ++ legacy ++ waitEvent
    combined.foreach { value =>
      val isa = value("isa").str
      val old = PartialFunction.cond(isa) { case "GFX6" | "GFX7" | "GFX8" => true }
      if (value("kind").str == "WAITCNT" && old)
        value("fields").arr.filter(_("field")("name").str == "VM").foreach { field =>
          field("field")("ranges") = ujson.Arr(ujson.Obj("offset" -> 0, "bits" -> 4, "padding" -> 0))
        }
      if (value("kind").str == "SRC") {
        val names = value("values")
        // LLVM provides assembler aliases absent from some AMD XML generations.
        val modern = isa.startsWith("RDNA3") || ModernSourceIsas(isa)
        names("null") = codes(if (modern) "SGPR_NULL_gfx11plus" else "SGPR_NULL_gfxpre11")
        ImplicitSources.foreach { name =>
          names(name.toLowerCaseRoot) = codes(name)
        }
        if (old) {
          names.obj.keys.filter(_.startsWith("ttmp")).toVector.foreach(names.obj.remove(_))
          (constants("TTMP_VI_MIN") to constants("TTMP_VI_MAX")).zipWithIndex.foreach { case (code, index) =>
            names(s"ttmp$index") = code.toString
          }
          TrapSources.foreach(name => names(name.toLowerCaseRoot) = codes(name))
        }
        if (isa == "GFX7") flat.foreach { case (name, (ci, _)) =>
          names(name.toLowerCaseRoot.replace("flat_scr_", "flat_scratch_")) = ci
        }
        if (isa == "GFX6" || isa == "GFX7") { val _ = names.obj.remove("0.15915494") }
        // A register pair uses its low selector; spelling is shared with LLVM assembly.
        names.obj.toVector.filter(_._1.endsWith("_lo")).foreach { case (name, code) =>
          if (names.obj.get(name.stripSuffix("_lo") + "_hi").exists(_.str.toInt == code.str.toInt + 1))
            names(name.stripSuffix("_lo")) = code
        }
      }
    }
    combined
  }

  private def lowBitsField(name: String, bits: Int): ujson.Obj =
    ujson.Obj("name" -> name, "ranges" -> ujson.Arr(ujson.Obj("offset" -> 0, "bits" -> bits, "padding" -> 0)))

  private def bitRanges(field: Element): Vector[ujson.Obj] =
    directChildren(requiredDirect(field, "BitLayout"), "Range").sortBy(_.getAttribute("Order").toInt).map { range =>
      ujson.Obj(
        "offset"  -> requiredText(range, "BitOffset").toInt,
        "bits"    -> requiredText(range, "BitCount").toInt,
        "padding" -> optionalDirect(range, "Padding").map(requiredText(_, "BitCount").toInt).getOrElse(0)
      )
    }

  private def parseOperandLayouts(input: Path): Vector[ujson.Value] = {
    val document = xmlDocument(input)
    val isa      = input.getFileName.toString.stripPrefix("amdgpu_isa_").stripSuffix(".xml").toUpperCaseRoot
    val nodes    = document.getElementsByTagName("OperandType")
    Vector.tabulate(nodes.getLength)(nodes.item(_)).collect { case element: Element => element }.flatMap { operand =>
      optionalDirect(operand, "OperandTypeName").map { name =>
        val fields = optionalDirect(operand, "MicrocodeFormat").flatMap(optionalDirect(_, "BitMap"))
          .fold(Vector.empty[Element])(directChildren(_, "Field")).map { field =>
            val names =
              optionalDirect(field, "FieldPredefinedValues").fold(Vector.empty[Element])(
                directChildren(_, "PredefinedValue")
              )
                .flatMap(value =>
                  scala.util.Try(BigInt(requiredText(value, "Value"))).toOption.map(number =>
                    requiredText(value, "Name") -> ujson.Str(number.toString)
                  )
                )
            ujson.Obj(
              "field" -> ujson.Obj(
                "name"   -> requiredText(field, "FieldName"),
                "ranges" -> ujson.Arr.from(bitRanges(field))
              ),
              "names" -> ujson.Obj.from(names)
            )
          }
        val values = optionalDirect(operand, "OperandPredefinedValues")
          .fold(Vector.empty[Element])(directChildren(_, "PredefinedValue")).flatMap { value =>
            scala.util.Try(BigInt(requiredText(value, "Value"))).toOption.map(number =>
              requiredText(value, "Name").toLowerCaseRoot -> ujson.Str(number.toString)
            )
          }
        ujson.Obj(
          "isa"    -> isa,
          "kind"   -> name.getTextContent.stripPrefix("OPR_"),
          "fields" -> ujson.Arr.from(fields),
          "values" -> ujson.Obj.from(values)
        )
      }
    }
  }

  private def parseOfficialXML(input: Path): Vector[Instruction] = {
    val fileName = input.getFileName.toString
    val isa      = fileName.stripPrefix("amdgpu_isa_").stripSuffix(".xml").toUpperCaseRoot
    val document = xmlDocument(input)
    val nodes    = document.getElementsByTagName("Instruction")
    Vector.tabulate(nodes.getLength) { index =>
      val instruction = element(nodes.item(index), "Instruction")
      val name        = requiredText(instruction, "InstructionName").toLowerCaseRoot
      directChildren(requiredDirect(instruction, "InstructionEncodings"), "InstructionEncoding").map { encoding =>
        val originalFormat = requiredText(encoding, "EncodingName").stripPrefix("ENC_")
        val format         = if (name.startsWith("v_bitop3_")) originalFormat.replace("VOP3", "VOP3_BITOP3")
        else if (ByteSelectConversions(name)) originalFormat.replace("VOP3", "VOP3_BYTESEL")
        else originalFormat
        val opcode   = parseRadix(requiredDirect(encoding, "Opcode"))
        val operands = optionalDirect(encoding, "Operands")
          .fold(Vector.empty[Element])(directChildren(_, "Operand"))
          .map { operand =>
            Operand(
              field = optionalText(operand, "FieldName").getOrElse(""),
              kind = requiredText(operand, "OperandType").stripPrefix("OPR_"),
              input = booleanAttribute(operand, "Input"),
              output = booleanAttribute(operand, "Output"),
              implicitOperand = booleanAttribute(operand, "IsImplicit"),
              bits = requiredText(operand, "OperandSize").toInt
            )
          }
        val normalisedFormat   = if (format == "VOP2_VOP_SDWA_SDST_ENC") "VOP2_VOP_SDWA" else format
        val normalisedOperands = operands.map { operand =>
          if (format == "VOP2_VOP_SDWA_SDST_ENC" && operand.field == "SDST") operand.copy(field = "", kind = "VCC")
          else if (name == "s_wait_event" && operand.field == "SIMM16") operand.copy(kind = "WAIT_EVENT")
          else operand
        }
        Instruction(
          name,
          isa,
          normalisedFormat,
          opcode,
          normalisedOperands,
          optionalDirect(instruction, "FunctionalGroup").toVector.flatMap(group =>
            optionalDirect(
              group,
              "FunctionalSubgroups"
            ).fold(Vector.empty[Element])(directChildren(_, "Subgroup")).map(_.getTextContent.trim)
          )
        )
      }
    }.flatten
  }

  private def parseACO(source: Path, python: String): Vector[Instruction] = {
    if (!Files.isRegularFile(source)) throw IllegalArgumentException(s"missing Mesa ACO opcode source: $source")
    val rows = Vector.newBuilder[String]
    val exit = Process(Seq(python, "-B", "-c", ACOExporter, source.toString))
      .!(ProcessLogger(rows += _, Console.err.println(_)))
    if (exit != 0) throw IllegalStateException(s"ACO exporter failed with exit code $exit")
    rows.result().map { row =>
      val columns = Text.splitDelimited(row, '\t')
      if (columns.size != 7) throw IllegalArgumentException(s"invalid ACO exporter row: $row")
      val operands =
        if (columns(5).isEmpty) Vector.empty
        else
          Text.splitDelimited(columns(5), ';').map { encoded =>
            val fields = Text.splitDelimited(encoded, ',')
            if (fields.size != 6) throw IllegalArgumentException(s"invalid ACO operand: $encoded")
            Operand(
              "",
              fields(0),
              fields(3) == "1",
              fields(4) == "1",
              fields(5) == "1",
              fields(1).toInt * fields(2).toInt
            )
          }
      val scalarFields = ScalarFormats(columns(2))
      val normalised   = operands.zipWithIndex.map { case (operand, index) =>
        if (!scalarFields || operand.implicitOperand) operand
        else if (operand.output) operand.copy(field = "SDST")
        else {
          val source = operands.take(index).count(value => value.input && !value.implicitOperand)
          operand.copy(field = s"SSRC$source")
        }
      }
      Instruction(
        columns(1),
        columns(0),
        columns(2),
        columns(3).toInt,
        normalised
      )
    }
  }

  private val Relocation = raw"ELF_RELOC\((R_AMDGPU_[A-Z0-9_]+),\s*([0-9]+)\)".r

  private def renderConstants(result: Extracted, references: Path): String = {
    val relocations = Relocation.findAllMatchIn(Files.readString(
      references.resolve("llvm/llvm/include/llvm/BinaryFormat/ELFRelocs/AMDGPU.def"),
      StandardCharsets.UTF_8
    ))
      .map(value => s"  val ${value.group(1)}: Int = ${value.group(2)}").mkString("\n")
    val header =
      Files.readString(references.resolve("llvm/llvm/lib/Target/AMDGPU/SIDefines.h"), StandardCharsets.UTF_8)
    GeneratedHeader.scala("encoding constants from pinned AMD and LLVM inputs") +
      s"package net.kurobako.gemstone.isa.amdgpu\n\nimport net.kurobako.gemstone.constraint.*\n\n${GeneratedHeader.annotation()}@SuppressWarnings(Array(\"baumkuchen:unused-member\"))\nprivate object EncodingConstants {\n" +
      renderOperandConstants(header) + renderSharedConstants(result, references) + relocations + "\n" +
      renderELFMachines(Files.readString(
        references.resolve("llvm/llvm/include/llvm/BinaryFormat/ELF.h"),
        StandardCharsets.UTF_8
      )) + renderKernelCodeProperties(Files.readString(
        references.resolve("llvm/llvm/include/llvm/Support/AMDHSAKernelDescriptor.h"),
        StandardCharsets.UTF_8
      )) + "}\n"
  }

  private val Property = raw"KERNEL_CODE_PROPERTY\(([A-Z0-9_]+),\s*([0-9]+),\s*([0-9]+)\)".r

  def renderKernelCodeProperties(header: String): String = {
    val properties = Property.findAllMatchIn(header).filterNot(_.group(1).startsWith("RESERVED")).map { value =>
      val mask = ((1L << value.group(3).toInt) - 1L) << value.group(2).toInt
      s"    val ${value.group(1)}: Int = 0x${mask.toHexString}"
    }.toVector
    require(properties.nonEmpty, "LLVM HSA descriptor has no kernel code properties")
    s"  object KernelCodeProperty {\n${properties.mkString("\n")}\n  }\n"
  }

  private val Machine = """X\((0x[0-9a-fA-F]+),\s*EF_AMDGPU_MACH_AMDGCN_[A-Z0-9_]+,\s*"([^"]+)"\)""".r

  private val Mask = raw"EF_AMDGPU_MACH\s*=\s*(0x[0-9a-fA-F]+)".r

  def renderELFMachines(header: String): String = {
    val mask = Mask.findFirstMatchIn(header).map(_.group(1))
      .getOrElse(throw IllegalArgumentException("LLVM ELF header has no AMDGPU machine mask"))
    val machines = Machine.findAllMatchIn(header).map(value => value.group(1) -> value.group(2)).toVector
    require(machines.nonEmpty, "LLVM ELF header has no AMDGCN machine names")
    val entries = machines.map { case (code, name) => s"      $code -> ${Emission.literal(name)}" }.mkString(",\n")
    s"  object ELFMachine {\n    val Mask: Int = $mask\n    val Names: Map[Int, String] = Map(\n$entries\n    )\n  }\n"
  }

  private def renderImageDimensions(tables: ujson.Value): String = {
    val dimensions = imageMetadata(tables)("dimensions").arr.toVector
    val cases      = dimensions.zipWithIndex.map { case (value, encoding) =>
      val spelling = value("name").str
      val name     = "Image" + spelling.split('_').map(_.toLowerCaseRoot.capitalize).mkString
      s"  case $name extends ImageDimension(encoding = $encoding, assemblyName = ${Emission.literal("SQ_RSRC_IMG_" + spelling)}, coordinates = ${value("coordinates").num.toInt}, gradients = ${value("gradients").num.toInt})"
    }.mkString("\n")
    GeneratedHeader.scala("from LLVM AMDGPUDimProps") +
      s"""|package net.kurobako.gemstone.isa.amdgpu
          |
          |${GeneratedHeader.annotation()}enum ImageDimension(val encoding: Int, val assemblyName: String, val coordinates: Int, val gradients: Int) derives CanEqual {
          |$cases
          |}
          |""".stripMargin
  }

  private val SourceName = "VSRC[0-9]+".r

  private def renderDomains(result: Extracted): Vector[(String, String)] = {
    val operands = result.records.flatMap(_.operands)
    val domains  = Vector(
      "EncodingFormat" -> (result.records.map(_.format) ++ result.layouts.map(_("format").str) ++ Vector("GLOBAL")),
      "EncodingField"  -> (operands.map(_.field) ++ result.layouts.flatMap(
        _("fields").arr.map(_("name").str)
      ) ++ result.operands.flatMap(_("fields").arr.map(_("field")("name").str))),
      "OperandKind"      -> (operands.map(_.kind) ++ result.operands.map(_("kind").str)),
      "InstructionGroup" -> result.records.flatMap(_.groups),
      "SpecialSource"    -> result.operands.flatMap(_.obj.get("values").toVector.flatMap(_.obj.keys)).filter(name =>
        name.startsWith("src_") && !name.startsWith("src_literal")
      )
    )
    domains.map { case (name, values) =>
      val names = values.filter(_.nonEmpty).distinct.sorted
      val known = names.toSet
      val cases = names.map(value => s"  case `$value`").mkString("\n")
      def predicate(method: String)(accepts: String => Boolean): String = {
        val selected = names.view.filter(accepts).map(value => s"`$value`").mkString(" | ")
        if (selected.isEmpty) s"  def $method: Boolean = false"
        else s"  def $method: Boolean = this match { case $selected => true; case _ => false }"
      }
      def sourceModifier(method: String, prefix: String, suffix: String): String = {
        val associations = names.view.filter(SourceName.matches(_)).flatMap { source =>
          val modifier = prefix + source.stripPrefix("VSRC") + suffix
          Option.when(known(modifier))(s"case `$source` => Some(`$modifier`)")
        }.mkString("; ")
        s"  def $method: Option[EncodingField] = this match { $associations; case _ => None }"
      }
      val properties = if (name == "EncodingFormat") {
        val bases = names.view.filter(value => value.contains('_') && known(value.takeWhile(_ != '_'))).map(value =>
          s"    case `$value` => `${value.takeWhile(_ != '_')}`"
        ).mkString("\n")
        val literals = names.flatMap { value =>
          LiteralSuffixes.collect {
            case (bytes, suffix) if known(value + suffix) =>
              s"case (`$value`, $bytes) => Some(`${value + suffix}`)"
          }
        }.mkString("\n")
        Vector(
          s"  def base: EncodingFormat = this match {\n$bases\n    case _ => this\n  }",
          s"  def withLiteral(bytes: Int): Option[EncodingFormat] = (this, bytes) match {\n$literals\n case _ => None\n }",
          predicate("isImage")(value => value.startsWith("MIMG") || ModernImageFormats(value)),
          predicate("isNonSequentialImage")(_.contains("NSA")),
          predicate("isVector")(_.startsWith("VOP")),
          predicate("isVOP3")(_.startsWith("VOP3")),
          predicate("isFlat")(_.startsWith("FLAT")),
          predicate("isLiteral32")(_.endsWith("_INST_LITERAL")),
          predicate("isDpp")(_.contains("_VOP_DPP")),
          predicate("isSdwa")(_.contains("_VOP_SDWA")),
          predicate("isDpp8")(_.endsWith("DPP8")),
          predicate("isScratch")(_.endsWith("SCRATCH")),
          predicate("isBuffer")(PartialFunction.cond(_) { case "MTBUF" | "MUBUF" | "VBUFFER" => true }),
          predicate("isDataShare")(PartialFunction.cond(_) { case "DS" | "VDS" => true }),
          predicate("isVOP3PX")(_.startsWith("VOP3PX"))
        ).mkString("\n")
      } else if (name == "OperandKind") Vector(
        predicate("isImmediate")(_.startsWith("SIMM")),
        predicate("isVector")(_.startsWith("VGPR")),
        predicate("isSource")(_.startsWith("SRC")),
        predicate("isMessage")(_.startsWith("SENDMSG")),
        predicate("isScalarRegister")(_.startsWith("SREG")),
        predicate("isScalarSource")(_.startsWith("SSRC")),
        predicate("isScalarDestination")(_.startsWith("SDST")),
        predicate("isBareAccumulator")(_.startsWith("ACC")),
        predicate("isAccumulatorOnly")(PartialFunction.cond(_) { case "ACCVGPR" | "SRC_ACCVGPR" => true }),
        predicate("acceptsAccumulator")(_.contains("ACC")),
        predicate("acceptsConstant")(_.endsWith("OR_CONST")),
        predicate("excludesLiteral")(_.endsWith("NOLIT"))
      ).mkString("\n")
      else if (name == "EncodingField") Vector(
        "  def assemblyName: NonEmpty[String] = this match { " +
          names.map(value => s"case `$value` => ${Emission.literal(value.toLowerCaseRoot)}").mkString("; ") + " }",
        predicate("isVectorSource")(_.startsWith("VSRC")),
        sourceModifier("absoluteModifier", "SRC", "_ABS"),
        sourceModifier("negativeModifier", "SRC", "_NEG"),
        sourceModifier("extensionModifier", "SRC", "_SEXT"),
        predicate("isVectorDestination")(_.startsWith("VDST")),
        predicate("isDataOperand")(PartialFunction.cond(_) { case "DATA" | "DATA0" | "DATA1" | "VDATA" | "VDST" =>
          true
        }),
        predicate("isScale")(_.startsWith("SCALE_")),
        predicate("isImageAddress")(_.startsWith("VADDR")),
        predicate("isWait")(_.startsWith("WAIT_")),
        "  def index: Int = this match { " + names.view.filter(_.last.isDigit).map(value =>
          s"case `$value` => ${value.last.asDigit}"
        ).mkString("; ") + "; case _ => -1 }"
      ).mkString("\n")
      else ""
      val indexed = if (name == "EncodingField") IndexedFields.map { case (member, prefix, suffix) =>
        val selected = names.filter(_.matches(prefix + "[0-9]+" + suffix)).sortBy(value =>
          value.stripPrefix(prefix).stripSuffix(suffix).toInt
        )
        s"  val $member: Vector[EncodingField] = Vector(${selected.map(value => s"EncodingField.`$value`").mkString(", ")})"
      }.mkString("\n")
      else ""
      val constraintImport = if (name == "EncodingField") "import net.kurobako.gemstone.constraint.*\n" else ""
      name -> (GeneratedHeader.scala("from the pinned AMDGPU machine metadata") +
        s"""|package net.kurobako.gemstone.isa.amdgpu
            |
            |${constraintImport}import upickle.default.*
            |
            |object $name {
            |$indexed
            |  given ReadWriter[$name] = readwriter[String].bimap(_.toString, valueOf(_))
            |  private lazy val byName: Map[String, $name] = values.view.map(value => value.toString -> value).toMap
            |  def fromName(name: String): Option[$name] = byName.get(name)
            |}
            |
            |${GeneratedHeader.annotation()}enum $name derives CanEqual {
            |$cases
            |$properties
            |}
            |""".stripMargin)
    }
  }

  private def renderInstructions(records: Vector[Instruction]): InstructionSources = {
    val recordsByOpcode    = records.groupBy(_.name)
    val metadataEntries    = recordsByOpcode.toVector.sortBy(_._1)
    val distinctMetadata   = metadataEntries.map(_._2).distinct
    val metadataIds        = distinctMetadata.zipWithIndex.toMap
    val metadataIdByOpcode = metadataEntries.map { case (opcode, metadata) =>
      opcode -> metadataIds(metadata)
    }.toMap
    val metadata         = renderMetadataJson(distinctMetadata)
    val names            = uniqueCaseNames(metadataEntries.map(_._1))
    val definitions      = metadataEntries.map { case (opcode, _) => Definition(opcode, names(opcode)) }
    val definitionChunks = DefinitionDomain.values.toVector.flatMap { domain =>
      val domainDefinitions =
        definitions.filter(definition => definitionDomain(definition.opcode, recordsByOpcode) == domain)
      val shards = weightedChunks(domainDefinitions)(definition =>
        renderDefinitionCase(definition, metadataIdByOpcode(definition.opcode)).length
      )
      shards.map { members =>
        val range      = Option.when(shards.size > 1)(definitionRange(domain, members)).getOrElse("")
        val objectName = s"${domain.stem}${range}InstructionDefs"
        DefinitionChunk(s"${domain.directory}/$objectName.scala", objectName, members)
      }
    }
    if (definitionChunks.flatMap(_.definitions).size != definitions.size)
      throw IllegalStateException("definition-domain partition lost an AMDGPU opcode")
    if (
      definitionChunks.map(_.fileName).distinct.size != definitionChunks.size ||
      definitionChunks.map(_.objectName).distinct.size != definitionChunks.size
    ) throw IllegalStateException("definition-domain partition produced a duplicate source or object name")
    val definitionReferences = definitionChunks.flatMap { chunk =>
      chunk.definitions.map(definition => definition.opcode -> s"${chunk.objectName}.${definition.name}")
    }.toMap
    val comparisonEntries = definitionReferences.toVector.flatMap { case (opcode, reference) =>
      comparisonClassification(opcode).map(classification => reference -> classification)
    }.sortBy(_._1)
    val renderedComparisons = comparisonEntries.map { case (reference, classification) =>
      s"    $reference -> ComparisonOpcode(ComparisonKind.${classification.kind}, ComparisonRelation.${classification.relation}, updateExec = ${classification.updateExec})"
    }.mkString(",\n")
    val definitionChunkNames = definitionChunks.map(chunk => s"${chunk.objectName}.definitions").mkString(", ")
    val registry             = GeneratedHeader.scala("from the pinned AMDGPU instruction metadata") +
      s"""|package net.kurobako.gemstone.isa.amdgpu
        |
        |import Opcode.*
        |import net.kurobako.gemstone.collection.*
        |import net.kurobako.gemstone.string.*
        |
        |${GeneratedHeader.annotation()}object InstructionDefs {
        |  val ComparisonOpcodes: Map[Opcode, ComparisonOpcode] = Map(
        |$renderedComparisons
        |  )
        |
        |  val definitions: Vec[Opcode] = Vec($definitionChunkNames).flatten
        |
        |  private lazy val byName = definitions.groupBy(_.mnemonic.raw)
        |  def forOpcode(name: String): Vec[Opcode] = byName.getOrElse(name.toLowerCaseRoot, Vec.empty)
        |}
        |""".stripMargin
    val definitionSources = definitionChunks.map { chunk =>
      chunk.fileName -> renderDefinitionChunkFile(chunk.objectName, chunk.definitions, metadataIdByOpcode)
    } :+ ("InstructionDefs.scala" -> registry)
    val opcodes = GeneratedHeader.scala("from the pinned AMDGPU instruction metadata") +
      """|package net.kurobako.gemstone.isa.amdgpu
        |import net.kurobako.gemstone.constraint.*
        |import net.kurobako.gemstone.collection.*
        |
        |object Opcode {
        |  enum ComparisonKind derives CanEqual {
        |    case Signed16, Signed32, Signed64, Unsigned16, Unsigned32, Unsigned64
        |    case Float16, Float32, Float64, ClassFloat32, ClassFloat64
        |  }
        |
        |  enum ComparisonRelation derives CanEqual {
        |    case Equal, NotEqual, OrderedNotEqual, Less, LessOrEqual, Greater, GreaterOrEqual
        |    case NotLess, NotLessOrEqual, NotGreater, NotGreaterOrEqual
        |    case Ordered, Unordered, NotLessOrGreater, Class
        |  }
        |
        |  case class ComparisonOpcode(
        |      kind: ComparisonKind,
        |      relation: ComparisonRelation,
        |      updateExec: Boolean
        |  )
        |}
        |
        |abstract class Opcode(val mnemonic: NonEmpty[String], private val metadataId: Int) derives CanEqual {
        |  lazy val meta: Metadata = Metadata.Catalogue.metadata(metadataId)
        |
        |  /** The encodings of the opcode that `target` supports. */
        |  def encodings(target: Target): Vec[Metadata.Encoding] = meta.encodings.filter(_.supports(target))
        |  override final def toString: String = mnemonic
        |}
        |""".stripMargin
    InstructionSources(opcodes, metadata, definitionSources)
  }

  private def renderDefinitionChunkFile(
      objectName: String,
      definitions: Vector[Definition],
      metadataIdByOpcode: Map[String, Int]
  ): String =
    GeneratedHeader.scala("from the pinned Mesa ACO and AMD machine-readable ISA inputs") +
      s"""|package net.kurobako.gemstone.isa.amdgpu
          |
          |import net.kurobako.gemstone.constraint.*
          |import net.kurobako.gemstone.collection.*
          |
          |${renderDefinitionChunk(objectName, definitions, metadataIdByOpcode)}
        |""".stripMargin

  private def comparisonClassification(opcode: String): Option[ComparisonClassification] = {
    val (updateExec, prefix) =
      if (opcode.startsWith("v_cmpx_")) true -> "v_cmpx_"
      else if (opcode.startsWith("s_cmp_")) false -> "s_cmp_"
      else false                                  -> "v_cmp_"
    Option.when(opcode.startsWith(prefix))(opcode.drop(prefix.length)).flatMap { body =>
      val separator = body.lastIndexOf('_')
      Option.when(separator > 0)(body.take(separator) -> body.drop(separator + 1)).flatMap {
        case ("class", "f32")         => Some(ComparisonClassification("ClassFloat32", "Class", updateExec))
        case ("class", "f64")         => Some(ComparisonClassification("ClassFloat64", "Class", updateExec))
        case (relationName, kindName) =>
          val relation = relationName match {
            case "eq"         => Some("Equal")
            case "ne" | "neq" => Some("NotEqual")
            case "lg"         => Some("OrderedNotEqual")
            case "lt"         => Some("Less")
            case "le"         => Some("LessOrEqual")
            case "gt"         => Some("Greater")
            case "ge"         => Some("GreaterOrEqual")
            case "nlt"        => Some("NotLess")
            case "nle"        => Some("NotLessOrEqual")
            case "ngt"        => Some("NotGreater")
            case "nge"        => Some("NotGreaterOrEqual")
            case "o"          => Some("Ordered")
            case "u"          => Some("Unordered")
            case "nlg"        => Some("NotLessOrGreater")
            case _            => None
          }
          val kind = kindName match {
            case "i16" => Some("Signed16")
            case "i32" => Some("Signed32")
            case "i64" => Some("Signed64")
            case "u16" => Some("Unsigned16")
            case "u32" => Some("Unsigned32")
            case "u64" => Some("Unsigned64")
            case "f16" => Some("Float16")
            case "f32" => Some("Float32")
            case "f64" => Some("Float64")
            case _     => None
          }
          for {
            parsedRelation <- relation
            parsedKind     <- kind
          } yield ComparisonClassification(parsedKind, parsedRelation, updateExec)
      }
    }
  }

  private def definitionDomain(
      opcode: String,
      metadataByOpcode: Map[String, Vector[Instruction]]
  ): DefinitionDomain = {
    val formats                               = metadataByOpcode(opcode).view.map(_.format).toSet
    def hasPrefix(prefixes: String*): Boolean = formats.exists(format => prefixes.exists(format.startsWith(_)))

    if (
      formats.contains("VOP3P_MFMA") ||
      MatrixMnemonics.exists(opcode.contains(_))
    ) DefinitionDomain.Matrix
    else if (hasPrefix("VOPD")) DefinitionDomain.DualVector
    else if (hasPrefix("VOPC")) DefinitionDomain.VectorCompare
    else if (hasPrefix("VOP1", "ILLEGAL")) DefinitionDomain.VOP1
    else if (hasPrefix("VOP2")) DefinitionDomain.VOP2
    else if (hasPrefix("VOP3P")) DefinitionDomain.PackedVector
    else if (hasPrefix("VOP3")) DefinitionDomain.VOP3
    else if (hasPrefix("VINTERP", "VINTRP")) DefinitionDomain.Interpolation
    else if (hasPrefix("SOP1")) DefinitionDomain.SOP1
    else if (hasPrefix("SOP2")) DefinitionDomain.SOP2
    else if (hasPrefix("SOPC")) DefinitionDomain.SOPC
    else if (hasPrefix("SOPK")) DefinitionDomain.SOPK
    else if (hasPrefix("SOPP")) DefinitionDomain.SOPP
    else if (formats.contains("SMEM")) DefinitionDomain.ScalarMemory
    else if (hasPrefix("DS", "VDS")) DefinitionDomain.DataShare
    else if (hasPrefix("MUBUF", "MTBUF", "VBUFFER")) DefinitionDomain.Buffer
    else if (hasPrefix("FLAT", "GLOBAL", "SCRATCH", "VFLAT", "VGLOBAL", "VSCRATCH")) DefinitionDomain.Flat
    else if (hasPrefix("MIMG", "VIMAGE", "VSAMPLE")) DefinitionDomain.Image
    else if (hasPrefix("EXP", "VEXPORT")) DefinitionDomain.Export
    else if (formats.contains("LDSDIR")) DefinitionDomain.LDSDirect
    else
      throw IllegalArgumentException(
        s"AMDGPU opcode '$opcode' has no definition domain for formats ${formats.toVector.sorted.mkString(", ")}"
      )
  }

  private inline def weightedChunks[A](values: Vector[A])(inline weight: A => Int): Vector[Vector[A]] =
    values.foldLeft(Vector.empty[(Int, Vector[A])]) { (chunks, value) =>
      val valueWeight = weight(value)
      chunks.lastOption match {
        case Some((currentWeight, current))
            if current.nonEmpty && currentWeight + valueWeight <= MaximumDefinitionSourceCharacters =>
          chunks.updated(chunks.size - 1, (currentWeight + valueWeight, current :+ value))
        case _ => chunks :+ (valueWeight -> Vector(value))
      }
    }.map(_._2)

  private def definitionRange(
      domain: DefinitionDomain,
      definitions: Vector[Definition]
  ): String = {
    val first = rangeEndpoint(domain, definitions.head.name)
    val last  = rangeEndpoint(domain, definitions.last.name)
    if (first == last) first else s"${first}To$last"
  }

  private def rangeEndpoint(domain: DefinitionDomain, name: String): String = {
    val prefixes = domain match {
      case DefinitionDomain.SOP1 | DefinitionDomain.SOP2 | DefinitionDomain.SOPC | DefinitionDomain.SOPK |
          DefinitionDomain.SOPP => Vector("S")
      case DefinitionDomain.VOP1 | DefinitionDomain.VOP2 | DefinitionDomain.VOP3 |
          DefinitionDomain.PackedVector | DefinitionDomain.DualVector | DefinitionDomain.Interpolation |
          DefinitionDomain.Matrix => Vector("V")
      case DefinitionDomain.VectorCompare => Vector("VCmpx", "VCmp")
      case DefinitionDomain.DataShare     => Vector("Vds", "Ds")
      case DefinitionDomain.Image         => Vector("Image")
      case DefinitionDomain.Export        => Vector("VExport", "Exp")
      case _                              => Vector.empty
    }
    prefixes.collectFirst { case prefix if name.startsWith(prefix) => name.drop(prefix.length) }.getOrElse(name)
  }

  private def renderDefinitionChunk(
      objectName: String,
      definitions: Vector[Definition],
      metadataIdByOpcode: Map[String, Int]
  ): String = {
    val cases = definitions.map(definition => renderDefinitionCase(definition, metadataIdByOpcode(definition.opcode)))
    val references = definitions.map(_.name).mkString(", ")
    s"""|${GeneratedHeader.annotation()}private object $objectName {
        |${cases.mkString("\n")}
        |
        |  val definitions: Vec[Opcode] = Vec($references)
        |}""".stripMargin
  }

  private def renderDefinitionCase(definition: Definition, metadataId: Int): String =
    s"""|  case object ${definition.name} extends Opcode(${Emission.literal(definition.opcode)}, $metadataId)
        |""".stripMargin

  def renderMetadataJson(metadata: Vector[Vector[Instruction]]): String = {
    val records = metadata.flatten
    val strings = records.flatMap { record =>
      Vector(record.isa, record.format) ++ record.groups ++ record.processors ++
        record.operands.flatMap(operand => Vector(operand.kind) ++ Option.when(operand.field.nonEmpty)(operand.field))
    }.distinct.sorted
    val stringIds = strings.zipWithIndex.toMap
    val schemas   = records.map(_.operands).distinct
    val schemaIds = schemas.zipWithIndex.toMap
    val offsets   = metadata.foldLeft(Vector.empty[(Int, Int)]) { (acc, group) =>
      acc :+ (acc.lastOption.fold(0)((start, count) => start + count) -> group.size)
    }
    Vector(
      "{",
      "  \"strings\": [",
      strings.map(value => "    " + ujson.write(ujson.Str(value))).mkString(",\n"),
      "  ],",
      "  \"schemas\": [",
      schemas.map { operands =>
        "    " + operands.map { operand =>
          val field = if (operand.field.nonEmpty) stringIds(operand.field) else -1
          s"[$field,${stringIds(operand.kind)},${operand.implicitOperand},${operand.bits},${operand.output}]"
        }.mkString("[", ",", "]")
      }.mkString(",\n"),
      "  ],",
      "  \"encodings\": [",
      records.map { record =>
        val isa        = stringIds(record.isa)
        val format     = stringIds(record.format)
        val schema     = schemaIds(record.operands)
        val processors = if (record.processors.isEmpty) ""
        else
          s""","processors":${record.processors.map(stringIds).mkString("[", ",", "]")}"""
        s"""    {"isa":$isa,"format":$format,"opcode":${record.opcode},"schema":$schema,"groups":${record.groups.map(
            stringIds
          ).mkString("[", ",", "]")}$processors}"""
      }.mkString(",\n"),
      "  ],",
      "  \"metadata\": [",
      offsets.map { case (start, count) => s"    [$start,$count]" }.mkString(",\n"),
      "  ]",
      "}"
    ).mkString("\n") + "\n"
  }

  private def uniqueCaseNames(opcodes: Vector[String]): Map[String, String] = {
    val bases      = opcodes.map(opcode => opcode -> scalaCaseName(opcode))
    val collisions = bases.groupMap { case (_, name) => name.toLowerCaseRoot }(_._1)
    bases.map { case (opcode, base) =>
      val alternatives = collisions(base.toLowerCaseRoot).sorted
      opcode -> (if (alternatives.size == 1) base else s"${base}_${alternatives.indexOf(opcode) + 1}")
    }.toMap
  }

  private def scalaCaseName(opcode: String): String = Identifiers.upperCamel(opcode, "Opcode", "Opcode")

  private def parseRadix(element: Element): Int = {
    val radix = Option(element.getAttribute("Radix")).filter(_.nonEmpty).map(_.toInt).getOrElse(10)
    Integer.parseInt(element.getTextContent.trim, radix)
  }

  private def booleanAttribute(element: Element, name: String): Boolean =
    element.getAttribute(name).toLowerCaseRoot match {
      case "true"  => true
      case "false" => false
      case other   => throw IllegalArgumentException(s"invalid $name '$other'")
    }

  private def requiredText(element: Element, name: String): String =
    optionalText(element, name).getOrElse(throw IllegalArgumentException(s"missing <$name>"))

  private def optionalText(element: Element, name: String): Option[String] = {
    val nodes = element.getElementsByTagName(name)
    if (nodes.getLength == 0) None else Some(nodes.item(0).getTextContent.trim)
  }

  private def requiredDirect(element: Element, name: String): Element =
    optionalDirect(element, name).getOrElse(throw IllegalArgumentException(s"missing direct <$name>"))

  private def optionalDirect(element: Element, name: String): Option[Element] = directChildren(element, name).headOption

  private def element(node: Node, tag: String): Element = node match {
    case found: Element => found
    case _              => throw IllegalArgumentException(s"$tag node is not an XML element: $node")
  }

  private def directChildren(element: Element, name: String): Vector[Element] = {
    val children = element.getChildNodes
    Vector.tabulate(children.getLength)(children.item(_)).collect {
      case child: Element if child.getTagName == name => child
    }
  }

  private def write(output: Path, content: String): Unit = {
    val _ = GeneratedFiles.write(output, GeneratedScalaFormatter(output, content))
  }

}
