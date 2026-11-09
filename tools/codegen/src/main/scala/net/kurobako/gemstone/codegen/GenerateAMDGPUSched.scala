package net.kurobako.gemstone.codegen

import AMDGPUSchedModel.*
import java.nio.file.Files
import java.nio.file.Path
import scala.collection.mutable
import scala.sys.process.Process

/** Issue cycles and latencies from LLVM's AMDGPU scheduling models, joined to catalogue mnemonics. */
object GenerateAMDGPUSched {
  private val Notes =
    "Cycles are LLVM scheduling-model estimates: ReleaseAtCycles summed per resource across the write groups, " +
      "as TableGen merges them, divided by NumUnits (rounded up), maximised over resources, " +
      "as MCSchedModel::getReciprocalThroughput. " +
      "Records of one mnemonic and form must resolve alike, or codegen fails. " +
      "A mnemonic with plain and LDS records is keyed in each table. " +
      "An LDS-only mnemonic is keyed in the plain table only. " +
      "Latency is the LLVM WriteRes latency and is not used by the profile."

  private val ReportName       = "scheduling-report.md"
  private val ReportColumns    = Vector("processor", "model", "in scope", "scheduled")
  private val ReportAlignment  = Vector("---", "---", "---:", "---:")
  private val UnmatchedHeading = Vector("", "## Unmatched", "")

  case class Table(model: String, entries: Vector[(String, Entry)], lds: Vector[(String, Entry)])

  case class Rendered(resource: String, report: String)

  def generate(
      tablegen: Path,
      source: Path,
      includes: Vector[Path],
      dump: Path,
      catalogue: Vector[String],
      aliases: Vector[(String, String)],
      llvm: String,
      resource: Path
  ): Unit =
    try {
      dumpRecords(tablegen, source, includes, dump)
      val committed = Option.when(Files.exists(resource))(ujson.read(Files.readString(resource)))
      val rendered  = render(dump, catalogue, aliases, llvm, committed)
      GeneratedFiles.write(resource, rendered.resource)
      GeneratedFiles.write(resource.resolveSibling(ReportName), rendered.report)
    } finally {
      val _ = Files.deleteIfExists(dump)
    }

  def render(
      dump: Path,
      catalogue: Vector[String],
      aliases: Vector[(String, String)],
      llvm: String,
      committed: Option[ujson.Value]
  ): Rendered = {
    val processors      = processorsWithAliases(exportProcessors(dump, catalogue), aliases)
    val (tables, index) = indexTables(processors)
    val current         = processors.flatMap { p =>
      p.outcomes.collect { case (m, Right(_)) => (p.name, m, false) } ++
        p.ldsOutcomes.collect { case (m, Right(_)) => (p.name, m, true) }
    }.toSet
    val known     = catalogue.toSet
    val supported = AMDGPUDomainRules.Processors.values.flatten.toSet
    val dropped   = committed.toVector
      .flatMap(matched(_))
      .filterNot(current)
      .filter((p, m, _) => supported(p) && known(m))
      .sorted
    if (dropped.nonEmpty)
      throw IllegalStateException(
        s"${dropped.size} previously matched AMDGPU schedule entries no longer match: " +
          dropped.take(50).map { case (p, m, lds) => if (lds) s"$p $m lds" else s"$p $m" }.mkString("; ")
      )
    Rendered(renderResource(llvm, tables, index), renderReport(llvm, processors))
  }

  private def dumpRecords(tablegen: Path, source: Path, includes: Vector[Path], output: Path): Unit = {
    val command =
      Vector(tablegen.toString, "--print-records") ++ includes.flatMap(path => Vector("-I", path.toString)) :+
        source.toString
    val status = (Process(command) #> output.toFile).!
    require(status == 0, s"TableGen record dump failed with exit code $status")
  }

  private def exportProcessors(dump: Path, catalogue: Vector[String]): Vector[Processor] =
    AMDGPUSchedModel(AMDGPUSchedRecords.read(dump), catalogue).processors

  private def processorsWithAliases(
      processors: Vector[Processor],
      aliases: Vector[(String, String)]
  ): Vector[Processor] = {
    val byName = processors.map(processor => processor.name -> processor).toMap
    processors ++ aliases.map { case (alias, canonical) =>
      require(!byName.contains(alias), s"alias $alias names an LLVM processor")
      val target = byName.getOrElse(
        canonical,
        throw IllegalStateException(s"alias $alias targets unknown processor $canonical")
      )
      target.copy(name = alias)
    }
  }

  private def indexTables(processors: Vector[Processor]): (Vector[Table], Map[String, Int]) = {
    val keys  = mutable.LinkedHashMap.empty[Table, Int]
    val index = processors.sortBy(_.name).map { processor =>
      val table = Table(processor.model, scheduled(processor.outcomes), scheduled(processor.ldsOutcomes))
      processor.name -> keys.getOrElseUpdate(table, keys.size)
    }.toMap
    (keys.toVector.sortBy(_._2).map(_._1), index)
  }

  private def scheduled(outcomes: Map[String, Either[String, Entry]]): Vector[(String, Entry)] =
    outcomes.collect { case (mnemonic, Right(entry)) => mnemonic -> entry }.toVector.sortBy(_._1)

  private def matched(resource: ujson.Value): Set[(String, String, Boolean)] = {
    val tables = resource("tables").arr
    resource("processors").obj.toSet.flatMap { case (name, index) =>
      val table = tables(index.num.toInt)
      val plain = table("entries").obj.keys.map(mnemonic => (name, mnemonic, false))
      val lds   = table.obj.get("lds").toSeq.flatMap(_.obj.keys.map(mnemonic => (name, mnemonic, true)))
      plain ++ lds
    }
  }

  private def renderResource(llvm: String, tables: Vector[Table], index: Map[String, Int]): String = {
    val processors = index.toVector.sortBy(_._1).map { case (name, table) => s"    ${ujson.write(name)}: $table" }
    Vector(
      "{",
      s"  \"llvm\": ${ujson.write(llvm)},",
      s"  \"notes\": ${ujson.write(Notes)},",
      "  \"processors\": {",
      processors.mkString(",\n"),
      "  },",
      "  \"tables\": [",
      tables.map(renderTable(_)).mkString(",\n"),
      "  ]",
      "}"
    ).mkString("", "\n", "\n")
  }

  private def renderTable(table: Table): String =
    Vector(
      "    {",
      s"      \"model\": ${ujson.write(table.model)},",
      s"      \"lds\": ${renderEntries(table.lds)},",
      s"      \"entries\": ${renderEntries(table.entries)}",
      "    }"
    ).mkString("\n")

  private def renderEntries(entries: Vector[(String, Entry)]): String =
    if (entries.isEmpty) "{}"
    else
      entries
        .map { case (mnemonic, entry) =>
          s"        ${ujson.write(mnemonic)}: [${entry.latency}, ${entry.cycles}, ${ujson.write(entry.writes)}]"
        }
        .mkString("{\n", ",\n", "\n      }")

  private def renderReport(llvm: String, processors: Vector[Processor]): String = {
    val reasons = processors.flatMap(_.outcomes.values.collect { case Left(reason) => reason }).distinct.sorted
    val names   = processors.map(_.name).distinct.sorted
    val rows    = processors.sortBy(_.name).map { processor =>
      val counts    = reasons.map(reason => processor.outcomes.values.count(_ == Left(reason)).toString)
      val scheduled = processor.outcomes.values.count(_.isRight).toString
      val cells     = Vector(processor.name, processor.model, processor.outcomes.size.toString, scheduled) ++ counts
      cells.mkString("| ", " | ", " |")
    }
    val unmatched = processors
      .flatMap(p => p.outcomes.collect { case (mnemonic, Left(reason)) => (reason, mnemonic, p.name) })
      .groupMap { case (reason, mnemonic, _) => (reason, mnemonic) }(_._3)
      .toVector
      .groupMap { case ((reason, _), procs) => (reason, procs.sorted) } { case ((_, mnemonic), _) => mnemonic }
      .toVector
      .sortBy { case ((reason, procs), _) => (reason, procs.mkString(",")) }
      .map { case ((reason, procs), mnemonics) =>
        val scope = if (procs.size == names.size) "all processors" else procs.mkString(", ")
        s"- `$reason` for $scope (${mnemonics.size}): ${mnemonics.sorted.map(m => s"`$m`").mkString(", ")}"
      }
    Vector(
      "# AMDGPU scheduling resource",
      "",
      s"LLVM $llvm scheduling models joined to the opcode catalogue by exact mnemonic.",
      "Cycles are LLVM estimates from ReleaseAtCycles, not measured issue cycles. Latency is recorded and not used.",
      "",
      "In scope: catalogue opcodes with an LLVM record available on the processor, plus opcodes with no LLVM record.",
      "",
      (ReportColumns ++ reasons).mkString("| ", " | ", " |"),
      (ReportAlignment ++ reasons.map(_ => "---:")).mkString("| ", " | ", " |")
    ).appendedAll(rows).appendedAll(UnmatchedHeading).appendedAll(unmatched).mkString("", "\n", "\n")
  }
}
