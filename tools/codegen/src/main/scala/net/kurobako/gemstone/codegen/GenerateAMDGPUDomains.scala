package net.kurobako.gemstone.codegen

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.string.*
import scala.jdk.CollectionConverters.*
import scala.util.Using
import upickle.default.*

/** Builds the typed operand domains of every AMDGPU ISA and the report of where the sources disagree. */
object GenerateAMDGPUDomains {
  import AMDGPUDomainRules.*
  import AMDGPUOperandDomains.*

  private case class Request(references: Path, sourceRoot: Path, resourceRoot: Path, oracleRoot: Path)

  case class Table(isa: String, result: Result, mapping: AMDGPUOperandFields.Mapping)

  private val PackageRoot = "net/kurobako/gemstone/isa/amdgpu"

  private val DomainResource = "domains-[a-z0-9_]+\\.json".r

  private val command = Command("GenerateAMDGPUDomains", "Generate AMDGPU operand domains")((
    CodegenCommand.path("prepared-references"),
    CodegenCommand.path("generated-source-root"),
    CodegenCommand.path("generated-resource-root"),
    CodegenCommand.path("oracle-directory")
  ).mapN(Request(_, _, _, _)))

  def main(arguments: Array[String]): Unit = CodegenCommand.run(command, arguments) { request =>
    generate(request.references, request.sourceRoot, request.resourceRoot, request.oracleRoot)
  }

  def generate(references: Path, sourceRoot: Path, resourceRoot: Path, oracleRoot: Path): Unit = {
    val xml = Using.resource(Files.list(references.resolve("amd/xml"))) { stream =>
      stream.toList.asScala.toVector.filter(_.getFileName.toString.endsWith(".xml")).sortBy(_.toString)
    }.map { path =>
      val isa = path.getFileName.toString.stripPrefix("amdgpu_isa_").stripSuffix(".xml").toUpperCaseRoot
      isa -> OperandDomainXML.parse(GenerateAMDGPUISA.xmlDocument(path))
    }.toMap
    val llvm     = OperandDomainLLVM.read(references.resolve("llvm/llvm/lib/Target/AMDGPU"))
    val oracle   = OperandDomainOracle.read(oracleRoot)
    val resource = resourceRoot.resolve(PackageRoot)
    val metadata = ujson.read(Files.readString(resource.resolve("metadata.json"), StandardCharsets.UTF_8))
    val layouts  = ujson.read(Files.readString(resource.resolve("layouts.json"), StandardCharsets.UTF_8))
    val tables   =
      build(xml, llvm, oracle, AMDGPUOperandFields.readEncodings(metadata), AMDGPUOperandFields.readLayouts(layouts))
    val outputs = tables.map(table => table -> resource.resolve(s"domains-${table.isa.toLowerCaseRoot}.json"))
    outputs.foreach((table, path) => GeneratedFiles.write(path, renderTable(table)))
    GeneratedFiles.write(resource.resolve("domains-report.md"), renderReport(tables, xml))
    GeneratedFiles.removeUnexpected(
      resource,
      outputs.map(_._2).toSet,
      path => DomainResource.matches(path.getFileName.toString)
    )
    val types  = sourceRoot.resolve(PackageRoot).resolve("domain")
    val listed = tables.flatMap { table =>
      table.result.entries.map(entry => entry.id -> entry.domain) ++ table.mapping.narrowed.toVector
    }
    val (files, _) = AMDGPUDomainTypes.sources(listed, types)
    files.foreach((path, content) => GeneratedFiles.writeScala(path, content))
    GeneratedFiles.removeUnexpected(types, files.map(_._1).toSet, _ => true)
    check(tables, resource.resolve("domains-report.md"))
  }

  def unresolved(tables: Vector[Table]): Vector[Finding] =
    tables.flatMap(table => table.result.findings ++ table.mapping.findings).filter(_.resolution.isEmpty)

  /** Fails when a disagreement is neither covered by a rule nor listed in the override table. */
  def check(tables: Vector[Table], report: Path): Unit = {
    val open = unresolved(tables)
    if (open.nonEmpty)
      throw IllegalStateException(
        s"${open.size} unresolved operand domain disagreements, see $report:\n" +
          open.take(40).map(f => s"  ${f.isa} ${f.check} ${f.subject}: ${f.detail}").mkString("\n")
      )
  }

  def build(
      xml: Map[String, Vector[OperandDomainXML.TypeDefinition]],
      llvm: OperandDomainLLVM.Model,
      oracle: Vector[OperandDomainOracle.Observation],
      encodings: Vector[AMDGPUOperandFields.Encoding],
      layouts: Vector[AMDGPUOperandFields.Layout]
  ): Vector[Table] = {
    val parsed   = xml.map((isa, definitions) => isa -> entriesOf(isa, definitions))
    val bases    = ISAs.map(isa => isa -> combine(documents(isa).map(parsed))).toMap
    val universe = bases.values.flatMap(_.flatMap(_.domain.codes.map(_._1))).toSet ++ llvm.registers.map(_.asmName)
    ISAs.map { isa =>
      val local  = encodings.filter(_.isa == isa)
      val used   = local.flatMap(_.operands.collect { case o if !o.implicitOperand => o.kind }).toSet
      val merged = merge(isa, bases(isa), Overrides, Evidence(llvm, oracle, universe, used))
      val typed  = merged.copy(entries = immediates(isa, merged.entries, ImmediateRules))
      Table(isa, typed, AMDGPUOperandFields.map(isa, local, layouts, typed.entries))
    }
  }

  private def domainJson(domain: OperandDomain): String = ujson.write(writeJs(domain))

  private def renderTable(table: Table): String = {
    val entries = table.result.entries.sortBy(_.id)
    val special = Vector(
      AMDGPUOperandFields.Ignored    -> OperandDomain.Ignored,
      AMDGPUOperandFields.Unmodelled -> OperandDomain.Unmodelled
    )
    val domains = entries.map(entry => entry.id -> entry.domain) ++ table.mapping.narrowed ++ special
    val rules   = Alignment.filter(_.isa == table.isa)
    Vector(
      "{",
      s"""  "isa": ${ujson.write(table.isa)},""",
      s"""  "base": ${ujson.write(Derived.getOrElse(table.isa, table.isa))},""",
      "  \"domains\": {",
      domains.map((id, domain) => s"""    ${ujson.write(id)}: ${domainJson(domain)}""").mkString(",\n"),
      "  },",
      "  \"subtypes\": {",
      entries.filter(_.subtypes.nonEmpty).map(entry =>
        s"""    ${ujson.write(entry.id)}: ${ujson.write(entry.subtypes.map(_.toLowerCaseRoot))}"""
      ).mkString(",\n"),
      "  },",
      "  \"alignment\": [",
      rules.map(rule =>
        s"""    {"file": ${ujson.write(rule.file)}, "fromBits": ${rule.fromBits}, "alignment": ${rule.alignment}}"""
      ).mkString(",\n"),
      "  ],",
      "  \"signatures\": [",
      table.mapping.signatures.map { signature =>
        val fields = ujson.Obj.from(signature.fields.map((field, id) => field -> ujson.Str(id)))
        s"""    {"format": ${ujson.write(signature.format)}, "fields": ${ujson.write(fields)}}"""
      }.mkString(",\n"),
      "  ],",
      "  \"encodings\": {",
      table.mapping.encodings.map((format, pairs) =>
        s"""    ${ujson.write(format)}: ${pairs.map((opcode, signature) => s"[$opcode,$signature]").mkString(
            "[",
            ",",
            "]"
          )}"""
      ).mkString(",\n"),
      "  }",
      "}"
    ).mkString("\n") + "\n"
  }

  private val ReportIntro = Vector(
    "# AMDGPU operand domains",
    "",
    "Generated by `sbt codegen`; do not edit.",
    "",
    "## Rules",
    "",
    "- The AMD ISA XML decides. LLVM TableGen (`SIRegisterInfo.td`, `SIDefines.h`, operand classes) and the LLVM MC",
    "  oracle cross-check it.",
    "- GFX6 to GFX9 take the CDNA1 document and GFX11_7 takes RDNA3_5, plus the RDNA4 types it lacks.",
    "  The overrides below adjust them.",
    "- A name LLVM defines and the XML lacks stays out of the domain (llvm-only) unless the oracle accepts it, which",
    "  needs an override.",
    "- A name the oracle rejects needs an override unless another processor of the family accepts it; the family",
    "  domain is the union over its processors.",
    "- Immediate signedness and width follow the XML. The layout wins when a field is shorter than its type.",
    "  The immediate rules below replace the XML where the oracle accepts a different range.",
    "- A disagreement no rule or override resolves fails the build.",
    "- The domains replace the former `SRC` selectors: `null` exists from GFX10 (the XML omits it before and llvm-mc",
    "  rejects it on gfx900, gfx908, gfx90a, gfx942 and gfx950), the apertures from GFX9, `xnack_mask` from GFX8, and",
    "  GFX6 to GFX8 spell codes 108 to 111 `tba` and `tma` as llvm-mc does.",
    "",
    "## Summary",
    "",
    "| ISA | base | operand types | domains | signatures | encodings | field mappings | findings | unresolved |",
    "| --- | --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |"
  )

  private val OverridesHeading = Vector("", "## Overrides", "")

  private val ImmediatesHeading = Vector("", "## Immediates", "")

  private val FieldRemapsHeading = Vector("", "## Field remaps", "")

  private val KindAliasesHeading = Vector("", "## Kind aliases", "")

  private val AlignmentHeading = Vector("", "## Alignment", "")

  private val ClassPairsHeading = Vector("", "## Class pairs", "")

  private val DescriptionsHeading = Vector("", "## Descriptions", "")

  private def fieldMappings(table: Table): Int =
    table.mapping.encodings.values.flatMap(_.map(_._2)).map(id => table.mapping.signatures(id).fields.size).sum

  private def renderReport(
      tables: Vector[Table],
      xml: Map[String, Vector[OperandDomainXML.TypeDefinition]]
  ): String = {
    val summary = tables.map { table =>
      val all     = table.result.findings ++ table.mapping.findings
      val domains = table.result.entries.size + table.mapping.narrowed.size + 2
      s"| ${table.isa} | ${Derived.getOrElse(table.isa, table.isa)} | ${table.result.entries.size} | $domains | " +
        s"${table.mapping.signatures.size} | ${table.mapping.encodings.values.map(_.size).sum} | " +
        s"${fieldMappings(table)} | ${all.size} | ${all.count(_.resolution.isEmpty)} |"
    }
    val overrides =
      if (Overrides.isEmpty) Vector("None.") else Overrides.map(row => s"- ${row.isa}: ${row.reason} (${row.source})")
    val sections = tables.flatMap { table =>
      val all = (table.result.findings ++ table.mapping.findings).sortBy(f => (f.check, f.subject))
      if (all.isEmpty) Vector.empty
      else
        Vector("", s"## ${table.isa}", "") ++ all.map { f =>
          s"- ${f.check} `${f.subject}`: ${f.detail} -> ${f.resolution.getOrElse("UNRESOLVED")}"
        }
    }
    val immediates = ImmediateRules.map { row =>
      val isas = ISAs.filter(row.isas).mkString(", ")
      s"- $isas ${row.operand}: ${row.reason} (${row.source})"
    }
    val fieldRemaps = FieldRemaps.map { row =>
      val isas = ISAs.filter(row.isas).mkString(", ")
      s"- $isas ${row.format}.${row.field} (${row.kind} -> ${row.toField}:${row.toKind}): ${row.reason} (${row.source})"
    }
    val kindAliases = KindAliases.map { row =>
      val isas = ISAs.filter(row.isas).mkString(", ")
      s"- $isas ${row.from} -> ${row.to}: ${row.reason} (${row.source})"
    }
    val alignment = Alignment.map { row =>
      s"- ${row.isa} ${row.file} from ${row.fromBits} align ${row.alignment} (${row.source})"
    }
    val classPairs = ClassPairs.map { row =>
      s"- ${row.xmlType} <-> ${row.llvmClass} (${row.source})"
    }
    val descriptions = xml.toVector.sortBy(_._1).flatMap { (doc, defs) =>
      defs.filter(d => d.description.nonEmpty && d.description != "N/A").sortBy(_.name).map { d =>
        s"- $doc ${d.name}: ${d.description}"
      }
    }
    (ReportIntro ++ summary ++ OverridesHeading ++ overrides ++ ImmediatesHeading ++ immediates ++
      FieldRemapsHeading ++ fieldRemaps ++ KindAliasesHeading ++ kindAliases ++ AlignmentHeading ++ alignment ++
      ClassPairsHeading ++ classPairs ++ DescriptionsHeading ++ descriptions ++ sections)
      .mkString("\n") + "\n"
  }
}
