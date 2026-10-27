package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.string.*

/** Merges AMD XML operand types with LLVM tables and the MC oracle into one domain table per ISA. */
object AMDGPUOperandDomains {
  import AMDGPUDomainRules.*
  import OperandDomain.*
  import OperandDomainLLVM.*
  import OperandDomainOracle.*

  /** An operand type of one ISA; `name` is the XML type name without its `OPR_` prefix. */
  case class Entry(name: String, domain: OperandDomain, subtypes: Vector[String], source: String) derives CanEqual {
    def id: String = name.toLowerCaseRoot
  }

  /** A place where XML and another source differ; `resolution` is empty when no rule or override covers it. */
  case class Finding(
      isa: String,
      check: String,
      subject: String,
      detail: String,
      types: Vector[String],
      resolution: Option[String]
  ) derives CanEqual

  case class Result(entries: Vector[Entry], findings: Vector[Finding])

  /** Everything the checks compare an ISA against. */
  case class Evidence(llvm: Model, oracle: Vector[Observation], universe: Set[String], used: Set[String])

  private val Temp    = "ttmp[0-9]+".r
  private val Scratch = "flat_scratch_(?:lo|hi)".r
  private val Range   = raw"(s|ttmp|v)\[([0-9]+):([0-9]+)\]".r

  def entriesOf(source: String, definitions: Vector[OperandDomainXML.TypeDefinition]): Vector[Entry] =
    definitions.map { definition =>
      val domain = OperandDomainXML.domain(definition) match {
        case OperandDomain.Immediate(bits, _, low, high) if SignedImmediates(definition.name) =>
          OperandDomain.Immediate(bits, signed = true, min = low, max = high)
        case other => other
      }
      Entry(definition.name, domain, definition.subtypes, source)
    }

  /** The entries of the first document, then the types only later documents define. */
  def combine(documents: Vector[Vector[Entry]]): Vector[Entry] =
    documents.foldLeft(Vector.empty[Entry]) { (acc, next) =>
      val known = acc.map(_.name).toSet
      acc ++ next.filterNot(entry => known(entry.name))
    }

  def flatten(domain: OperandDomain): Vector[OperandDomain] = domain match {
    case Union(members) => members.flatMap(flatten(_))
    case other          => Vector(other)
  }

  /** Rewrites the named codes of `domain`, leaving immediates and partitions as they are. */
  def edit(domain: OperandDomain, change: Vector[(String, Int)] => Vector[(String, Int)]): OperandDomain = {
    val (valued, other) = flatten(domain).partition(_.codes.nonEmpty)
    union(compress(change(valued.flatMap(_.codes))) ++ other)
  }

  def apply(entry: Entry, row: Override): Entry = {
    val present = entry.domain.codes.map(_._1)
    val applies = row.anchor.fold(row.remove.exists(pattern => present.exists(_.matches(pattern))))(present.contains)
    if (!applies) entry
    else
      entry.copy(domain =
        edit(entry.domain, codes => codes.filterNot(code => row.remove.exists(code._1.matches(_))) ++ row.add)
      )
  }

  /** Applies the immediate rules of `isa`: each replaces the immediate of its operand type or adds one. */
  def immediates(isa: String, entries: Vector[Entry], rules: Vector[ImmediateRule]): Vector[Entry] = {
    val byOperand = rules.filter(_.isas(isa)).groupBy(_.operand)
    entries.map { entry =>
      byOperand.getOrElse(entry.name, Vector.empty).foldLeft(entry) { (current, rule) =>
        val rest = flatten(current.domain).filterNot { case Immediate(_, _, _, _) => true; case _ => false }
        current.copy(domain = union(rest :+ rule.immediate))
      }
    }
  }

  def variantFor(isa: String, name: String): String = name match {
    case Temp()    => trapVariant(isa)
    case Scratch() => scratchVariant(isa)
    case _         => scalarVariant(isa)
  }

  def merge(isa: String, base: Vector[Entry], overrides: Vector[Override], evidence: Evidence): Result = {
    val rows       = overrides.filter(_.isa == isa)
    val before     = findings(isa, base, evidence)
    val merged     = base.map(entry => rows.foldLeft(entry)(apply(_, _)))
    val after      = findings(isa, merged, evidence)
    val applied    = rows.filter(row => base.exists(entry => apply(entry, row) != entry))
    val appliedSet = applied.toSet
    val stale      =
      rows.filterNot(row => appliedSet(row) || row.resolves.exists(sub => before.exists(_.subject == sub)))
    require(stale.isEmpty, s"overrides change and resolve nothing on $isa: ${stale.map(_.reason).mkString("; ")}")
    val tagged = before.map { finding =>
      val row = rows.find(row => row.resolves(finding.subject))
      row.filter(candidate => candidate.isKeep || !after.exists(sameSubject(finding))).fold(finding) { found =>
        finding.copy(resolution = Some(s"override: ${found.reason}"))
      }
    }
    Result(merged, tagged ++ after.filterNot(finding => before.exists(sameSubject(finding))))
  }

  private def sameSubject(finding: Finding)(other: Finding): Boolean =
    finding.check == other.check && finding.subject == other.subject

  /** Every spelling the entries accept, including a register pair spelled by its low half. */
  def nameSet(entries: Vector[Entry]): Set[String] = {
    val codes = entries.flatMap(_.domain.codes).toSet
    val pairs = codes.collect {
      case (name, code) if name.endsWith("_lo") && codes((name.stripSuffix("_lo") + "_hi", code + 1)) =>
        name.stripSuffix("_lo")
    }
    codes.map(_._1) ++ pairs
  }

  private def findings(isa: String, entries: Vector[Entry], evidence: Evidence): Vector[Finding] = {
    val Evidence(llvm, oracle, universe, used) = evidence
    donorFindings(isa, entries) ++ registerFindings(isa, entries, evidence) ++ nameFindings(isa, entries, llvm, used) ++
      constantFindings(isa, entries, llvm) ++ targetFindings(isa, entries, llvm) ++ classFindings(isa, entries, llvm) ++
      subtypeFindings(isa, entries) ++ llvmOnlyFindings(isa, entries, evidence) ++
      oracleFindings(isa, entries, oracle, universe)
  }

  private def donorFindings(isa: String, entries: Vector[Entry]): Vector[Finding] =
    entries.filter(entry => entry.source != Derived.getOrElse(isa, isa)).map { entry =>
      Finding(
        isa,
        "donor-type",
        entry.name,
        s"type defined by the ${entry.source} document only",
        Vector(entry.name),
        Some(s"XML wins: the record set draws its operands from ${entry.source}")
      )
    }

  private def registerFindings(isa: String, entries: Vector[Entry], evidence: Evidence): Vector[Finding] = {
    val llvm = evidence.llvm
    val uses = entries.flatMap(entry => flatten(entry.domain).collect { case r: Registers => entry.name -> r })
    uses.groupBy(_._2.file).toVector.sortBy(_._1).flatMap { (file, group) =>
      val types                                                      = group.map(_._1).distinct
      def finding(detail: String, resolution: Option[String] = None) =
        Finding(isa, "register-extent", file, detail, types, resolution)
      file match {
        case "sgpr" =>
          val bound  = llvm.encValues(sgprBound(isa))
          val last   = group.map(_._2.last).max
          val seen   = evidence.oracle.exists(o => o.accepted && Processors(isa)(o.target) && o.tokens(s"s$last"))
          val reason = s"oracle accepts s$last on $isa; the LLVM bound is the disassembler's"
          Option.when(last != bound)(
            finding(s"XML s0..s$last, LLVM ${sgprBound(isa)} = $bound", Option.when(seen && last > bound)(reason))
          )
        case "ttmp" =>
          val (low, high) = trapVariant(isa) match {
            case "vi" => llvm.encValues("TTMP_VI_MIN")       -> llvm.encValues("TTMP_VI_MAX")
            case _    => llvm.encValues("TTMP_GFX9PLUS_MIN") -> llvm.encValues("TTMP_GFX9PLUS_MAX")
          }
          val shapes = group.map((_, r) => (r.code - r.first, r.code + r.last - r.first)).distinct
          Option.when(shapes.exists((start, _) => start != low) || shapes.map(_._2).max != high)(
            finding(s"XML ttmp codes ${shapes.map((s, e) => s"$s..$e").mkString(",")}, LLVM $low..$high")
          )
        case other =>
          val bound = llvm.fileLast(if (other == "accvgpr") "agpr" else other)
          val last  = group.map(_._2.last).max
          Option.when(last > bound)(finding(s"XML $other last $last, LLVM $bound"))
      }
    }
  }

  private def nameFindings(isa: String, entries: Vector[Entry], llvm: Model, used: Set[String]): Vector[Finding] = {
    val uses = entries.filter(_.name != "TGT").flatMap(entry =>
      flatten(entry.domain).collect { case Named(name, code) => (entry.name, name, code) }
    )
    val pairs = nameSet(entries)
    uses.groupBy(_._2).toVector.sortBy(_._1).flatMap { (name, group) =>
      val types      = group.map(_._1).distinct
      val codes      = group.map(_._3).distinct
      val defs       = llvm.variantsOf(name)
      val applicable = defs.filter(d => d.variant.isEmpty || d.variant == variantFor(isa, name))
      def finding(check: String, detail: String, resolution: Option[String] = None) =
        Some(Finding(isa, check, name, detail, types, resolution))
      if (applicable.nonEmpty)
        codes.find(code => !applicable.exists(_.code == code)).flatMap(code =>
          finding("register-code", s"XML $code, LLVM ${applicable.map(_.code).mkString("/")}")
        )
      else if (defs.nonEmpty) {
        val variants = defs.map(d => s"${d.variant}=${d.code}").mkString(",")
        finding("register-variant", s"LLVM defines $variants but none for $isa")
      } else if (pairs(name) && llvm.variantsOf(s"${name}_lo").nonEmpty) None
      else if (PseudoOperands(name) || !types.exists(used))
        finding("unknown-name", "XML name without an LLVM register", Some("XML pseudo-operand, never encoded"))
      else finding("unknown-name", s"XML codes ${codes.mkString(",")} without an LLVM register")
    }
  }

  private def constantFindings(isa: String, entries: Vector[Entry], llvm: Model): Vector[Finding] = {
    val parts                   = entries.flatMap(entry => flatten(entry.domain).map(entry.name -> _))
    val minimum                 = llvm.encValues("INLINE_INTEGER_C_MIN")
    val maximum                 = llvm.encValues("INLINE_INTEGER_C_POSITIVE_MAX")
    val floatLow                = llvm.encValues("INLINE_FLOATING_C_MIN")
    val floatHigh               = llvm.encValues("INLINE_FLOATING_C_MAX")
    def integerCode(value: Int) = if (value >= 0) minimum + value else maximum - value
    val integers                = parts.collect {
      case (name, IntegerRange(first, last, code))
          if code != integerCode(first) || code + math.abs(last - first) != integerCode(last) =>
        name
    }
    val floats = parts.collect {
      case (name, FloatSet(values)) if values.exists(c => c.code < floatLow || c.code > floatHigh) => name
    }
    val literals = parts.collect {
      case (name, Literal(bytes, code))
          if code != llvm.encValues(if (bytes == 4) "LITERAL_CONST" else "LITERAL64_CONST") =>
        name
    }
    Vector("inline-integer" -> integers, "inline-float" -> floats, "literal" -> literals).collect {
      case (check, types) if types.nonEmpty =>
        Finding(isa, check, check, s"XML codes outside the LLVM EncValues $check range", types.distinct, None)
    }
  }

  private def targetFindings(isa: String, entries: Vector[Entry], llvm: Model): Vector[Finding] =
    entries.filter(_.name == "TGT").flatMap(_.domain.codes.flatMap { (name, code) =>
      val detail = llvm.exportTargets.get(name) match {
        case Some(expected) if expected == code => None
        case Some(expected)                     => Some(s"XML $code, LLVM $expected")
        case None                               => Some(s"XML $code, no LLVM target")
      }
      detail.map(Finding(isa, "export-target", name, _, Vector("TGT"), None))
    })

  private def classFindings(isa: String, entries: Vector[Entry], llvm: Model): Vector[Finding] =
    ClassPairs.flatMap { pair =>
      for {
        entry <- entries.find(_.name == pair.xmlType)
        cls   <- llvm.operandClasses.get(pair.llvmClass)
        if entry.domain.isLiteral != cls.literal
      } yield Finding(
        isa,
        "operand-class",
        pair.xmlType,
        s"XML literal ${entry.domain.isLiteral}, LLVM ${pair.llvmClass} literal ${cls.literal}",
        Vector(pair.xmlType),
        None
      )
    }

  private def subtypeFindings(isa: String, entries: Vector[Entry]): Vector[Finding] = {
    val byName = entries.map(entry => entry.name -> entry).toMap
    entries.flatMap { parent =>
      val codes = parent.domain.codes.toSet
      parent.subtypes.flatMap { child =>
        def finding(detail: String, types: Vector[String]) =
          Some(Finding(isa, "subtype", s"${parent.name}>$child", detail, types, None))
        byName.get(child) match {
          case None        => finding("subtype has no operand type", Vector(parent.name))
          case Some(entry) =>
            val missing = entry.domain.codes.filterNot(codes)
            val sample  = missing.take(3).map(_._1).mkString(",")
            if (missing.isEmpty) None
            else finding(s"${missing.size} codes outside the parent, e.g. $sample", Vector(parent.name, child))
        }
      }
    }
  }

  /** Names LLVM defines for `isa` that no XML type accepts; XML wins, and the oracle may confirm it. */
  private def llvmOnlyFindings(isa: String, entries: Vector[Entry], evidence: Evidence): Vector[Finding] = {
    val available = nameSet(entries)
    val local     = evidence.oracle.filter(o => Processors(isa)(o.target))
    val rejected  = local.filterNot(_.accepted).groupMap(_.message)(_.target)
    val names     = evidence.llvm.registers.filter(r => r.variant.isEmpty || r.variant == variantFor(isa, r.asmName))
      .map(_.asmName).distinct.filterNot(name => available(name) || Temp.matches(name)).sorted
    names.map { name =>
      val detail =
        rejected.get(name).fold("oracle silent")(t => s"oracle rejects on ${t.distinct.sorted.mkString(",")}")
      Finding(isa, "llvm-only", name, s"LLVM defines it, the XML does not; $detail", Vector.empty, Some("XML wins"))
    }
  }

  private def oracleFindings(
      isa: String,
      entries: Vector[Entry],
      oracle: Vector[Observation],
      universe: Set[String]
  ): Vector[Finding] = {
    val available = nameSet(entries)
    val local     = oracle.filter(observation => Processors(isa)(observation.target))
    val accepted  = local.filter(_.accepted)
    val top       = entries.flatMap(entry => flatten(entry.domain).collect { case r: Registers => r.file -> r.last })
      .groupMapReduce(_._1)(_._2)(_ max _)
    val rejected = local.filterNot(_.accepted).flatMap { observation =>
      observation.message match {
        case Range(prefix, _, last) =>
          val file = RegisterFiles.collectFirst { case (file, p) if p == prefix => file }.getOrElse(prefix)
          Option.when(top.get(file).exists(_ >= last.toInt))(observation -> observation.message)
        case name => Option.when(available(name))(observation -> name)
      }
    }
    val contradictions = rejected.groupBy(_._2).toVector.sortBy(_._1).map { (name, group) =>
      val rejecting = group.map(_._1.target).distinct.sorted
      val accepting = accepted.collect { case o if o.tokens(name) => o.target }.distinct.sorted
      val others    = if (accepting.isEmpty) "none" else accepting.mkString(",")
      val note      = "processor-dependent: the family domain is the union over its processors"
      Finding(
        isa,
        "oracle-rejects",
        name,
        s"oracle rejects on ${rejecting.mkString(",")}; accepts on $others",
        Vector.empty,
        Option.when(accepting.nonEmpty)(note)
      )
    }
    val extra   = accepted.flatMap(o => (o.tokens & universe).diff(available).toVector.map(_ -> o.target))
    val missing = extra.groupBy(_._1).toVector.sortBy(_._1).map { (name, group) =>
      val targets = group.map(_._2).distinct.sorted.mkString(",")
      Finding(isa, "oracle-accepts", name, s"oracle accepts on $targets but XML lacks it", Vector.empty, None)
    }
    contradictions ++ missing
  }
}
