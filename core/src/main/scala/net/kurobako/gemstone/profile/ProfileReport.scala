package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** A [[Profile]] as JSON lines and as a text summary. The JSON schema is versioned by `schema`: one `launch` line, then
  * one `instruction` line for each static instruction that issued. Register pressure counts 32-bit register units, and
  * every line carries its `findings`: what it wastes, as a count in its own unit and as estimated cycles of the
  * resource it wastes, which the architecture's [[Architecture.Throughput]] bounds.
  */
object ProfileReport {
  val Schema: Int = 3

  private def ordered(counters: Profile.Counters): Vector[((Space, AccessKind), Profile.Memory)] =
    counters.memory.toVector.sortBy((key, _) => (key._1.ordinal, key._2.ordinal))

  private def scheduled(profile: Profile): Boolean = profile.instructions.exists(_._1.schedule.nonEmpty)

  val encodedPlanning: (String, String)  = ("compiler encoding", "the compiler's encoded issue stalls")
  val modelledPlanning: (String, String) = ("LLVM scheduling model", "LLVM scheduling-model estimates")

  private def planning(profile: Profile): Vec[(String, String)] =
    profile.instructions.flatMap(_._1.schedule).map {
      case Schedule.Encoded(_, _) => encodedPlanning
      case Schedule.Estimated(_)  => modelledPlanning
    }.distinct

  private def ranked(found: Iterable[Finding]): Vec[Finding] =
    Vec.from(found.toVector.sortBy(finding => (-finding.cycles, finding.what, placement(finding.instruction))))

  private def memory(counters: Profile.Counters): ujson.Arr = ujson.Arr.from(
    ordered(counters).map { case ((space, kind), m) =>
      ujson.Obj(
        "space"           -> space.toString,
        "kind"            -> kind.toString,
        "requests"        -> m.requests.toDouble,
        "bytes"           -> m.bytes.toDouble,
        "sectors"         -> m.sectors.toDouble,
        "idealSectors"    -> m.idealSectors.toDouble,
        "lines"           -> m.lines.toDouble,
        "wavefronts"      -> m.wavefronts.toDouble,
        "idealWavefronts" -> m.idealWavefronts.toDouble
      )
    }
  )

  private def counters(value: Profile.Counters, scheduled: Boolean): ujson.Obj = {
    val line = ujson.Obj(
      "issues"         -> value.issues.toDouble,
      "activeLanes"    -> value.activeLanes.toDouble,
      "subgroupLanes"  -> value.subgroupLanes.toDouble,
      "laneEfficiency" -> value.laneEfficiency,
      "divergent"      -> value.divergent.toDouble,
      "reconverging"   -> value.reconverging.toDouble,
      "memory"         -> memory(value)
    )
    if (scheduled) line("plannedCycles") = value.plannedCycles.toDouble
    line
  }

  private def findingJson(value: Finding): ujson.Obj = ujson.Obj(
    "what"   -> value.what,
    "count"  -> value.count.toDouble,
    "unit"   -> value.unit,
    "cycles" -> value.cycles,
    "source" -> ujson.Obj("kind" -> value.source.kind, "detail" -> value.source.detail)
  )

  private def costJson(value: Cost): ujson.Obj = {
    val fields = ujson.Obj(
      "cycles" -> value.cycles,
      "bound"  -> value.bound,
      "path"   -> value.path.fold[ujson.Value](ujson.Null)(ujson.Num(_)),
      "binds"  -> bindingName(value.binds),
      "floor"  -> value.floor.fold[ujson.Value](ujson.Null)(tier => ujson.Str(tier.toString)),
      "source" -> ujson.Obj("kind" -> value.source.kind, "detail" -> value.source.detail)
    )
    value.dramIfMissed.foreach(cycles => fields("dramIfMissed") = cycles)
    fields
  }

  def jsonLines(profile: Profile): Vec[String] = {
    val ranked     = findings(profile)
    val byLocation = ranked.groupBy(_.instruction)
    val priced     = costs(profile).groupBy(_.instruction)
    val launch     = counters(profile.total, scheduled(profile))
    launch("record") = "launch"
    launch("schema") = Schema
    launch("architecture") = profile.architecture.name
    if (scheduled(profile)) launch("plannedCyclesSource") = planning(profile).map(_._1).mkString(", ")
    profile.occupancy.foreach { occupancy =>
      launch("occupancy") = ujson.Obj(
        "workgroups"   -> occupancy.workgroups,
        "subgroups"    -> occupancy.subgroups,
        "maxSubgroups" -> occupancy.maxSubgroups,
        "limiters"     -> ujson.Arr.from(occupancy.limiters.toVector.sortBy(_.ordinal).map(_.toString))
      )
    }
    profile.pressure.foreach(pressure =>
      launch("registerPressure") = ujson.Obj("peak" -> pressure.peak, "mean" -> pressure.mean, "unit" -> "32-bit")
    )
    launch("findings") = ujson.Arr.from(totals(ranked).map(findingJson(_)))
    val instructions = profile.instructions.zipWithIndex.collect {
      case ((instruction, value), index) if value.issues > 0L =>
        val line = counters(value, instruction.schedule.nonEmpty)
        line("record") = "instruction"
        line("index") = index
        line("kernel") = instruction.kernel.raw
        instruction.function.foreach(name => line("function") = name.raw)
        instruction.address.foreach(address => line("address") = address.raw.toDouble)
        line("text") = instruction.text
        line("findings") = ujson.Arr.from(byLocation.getOrElse(instruction, Vec.empty[Finding]).map(findingJson(_)))
        priced.getOrElse(instruction, Vec.empty[Cost]).foreach(cost => line("cost") = costJson(cost))
        line
    }
    (launch +: instructions).map(ujson.write(_))
  }

  /** What an instruction wastes, as `count` of `unit` and as the `cycles` of the resource that waste occupies. The
    * `source` is the weakest among the architecture values those cycles rest on.
    */
  case class Finding(
      cycles: Double,
      count: Long,
      unit: String,
      what: String,
      source: Architecture.Source,
      instruction: ProfileStream.Instruction
  ) derives CanEqual

  enum Binding derives CanEqual {
    case Issue, LoadStore, Shared, Loads, Path
    case Arithmetic(pipe: Pipe)
  }

  enum Tier derives CanEqual {
    case L1, L2, Dram
  }

  def bindingName(binding: Binding): String = binding match {
    case Binding.Issue            => "issue"
    case Binding.LoadStore        => "load-store"
    case Binding.Shared           => "shared"
    case Binding.Loads            => "loads"
    case Binding.Path             => "path"
    case Binding.Arithmetic(pipe) => s"${pipe.toString.toLowerCaseRoot} pipe"
  }

  /** What an instruction costs in cycles of one multiprocessor: the larger of its throughput `bound` and its dependency
    * `path`. `binds` names the term that sets `cycles`. `floor` marks a path that rests on global memory latency. The
    * `source` is the weakest among the terms and the path that contribute.
    */
  case class Cost(
      cycles: Double,
      bound: Double,
      binds: Binding,
      path: Option[Double],
      floor: Option[Tier],
      source: Architecture.Source,
      instruction: ProfileStream.Instruction,
      dramIfMissed: Option[Double] = None
  ) derives CanEqual

  private def totals(found: Vec[Finding]): Vec[Finding] =
    ranked(found.groupBy(_.what).values.map { group =>
      group.head.copy(cycles = group.map(_.cycles).sum, count = group.map(_.count).sum)
    })

  private[profile] def sectorSourceOf(architecture: Architecture): Architecture.Source = Architecture.Source.weakest(
    architecture.global.sectorBytes.source,
    architecture.global.lineBytes.source,
    architecture.throughput.loadStore.source
  )

  private[profile] def conflictSourceOf(architecture: Architecture): Architecture.Source = Architecture.Source.weakest(
    architecture.shared.count.source,
    architecture.shared.bankBytes.source,
    architecture.throughput.shared.source
  )

  /** What each instruction wastes, ranked together by the cycles it costs: sectors fetched beyond the ideal at the L1
    * sector rate, bank-conflict wavefronts at the shared wavefront rate, and the issue slots idle lanes waste at the
    * subgroup issue rate. The cycles are throughput bounds of one multiprocessor, not timing; the raw count and its
    * unit stay in the finding.
    */
  def findings(profile: Profile): Vec[Finding] = {
    val architecture   = profile.architecture
    val rates          = architecture.throughput
    val sectorSource   = sectorSourceOf(architecture)
    val conflictSource = conflictSourceOf(architecture)
    val found          = profile.instructions.flatMap { (instruction, value) =>
      val sectors   = value.memory.values.map(m => m.sectors - m.idealSectors).sum
      val conflicts = value.memory.values.map(_.conflicts).sum
      val idle      = value.subgroupLanes - value.activeLanes
      val idleSlots = value.issues.toDouble * (1.0 - value.laneEfficiency)
      Vec(
        Finding(
          sectors.toDouble / rates.loadStore.value,
          sectors,
          "sectors",
          "excess sectors",
          sectorSource,
          instruction
        ),
        Finding(
          conflicts.toDouble / rates.shared.value,
          conflicts,
          "wavefronts",
          "bank-conflict wavefronts",
          conflictSource,
          instruction
        ),
        Finding(
          idleSlots / rates.issues.value,
          idle,
          "lanes",
          "idle lanes",
          rates.issues.source,
          instruction
        )
      ).filter(_.count > 0L)
    }
    ranked(found)
  }

  private def term(
      binding: Binding,
      work: Double,
      rate: Architecture.Sourced[Double],
      source: Architecture.Source
  ): Option[(Binding, Double, Architecture.Source)] =
    Option.when(work > 0.0 && rate.value > 0.0)((binding, work / rate.value, source))

  private def limitSourcesOf(
      limits: Architecture.Limits,
      limiters: Set[Occupancy.Limiter]
  ): Vec[Architecture.Source] = {
    val registers = Vec(
      limits.registerGranule.source,
      limits.partitions.source,
      limits.partitionRegisters.source,
      limits.laneRegisters.source
    ) ++ limits.workgroupRegisters.map(_.source)
    val shared = Vec(
      limits.reservedShared.source,
      limits.sharedGranule.source,
      limits.workgroupShared.source,
      limits.shared.source
    )
    val sources = Map(
      Occupancy.Limiter.Subgroups  -> Vec(limits.workgroupLanes.source, limits.subgroups.source),
      Occupancy.Limiter.Workgroups -> Vec(limits.workgroups.source),
      Occupancy.Limiter.Registers  -> registers,
      Occupancy.Limiter.Shared     -> shared
    )
    Vec.from(limiters).flatMap(sources)
  }

  def costs(profile: Profile): Vec[Cost] = {
    val architecture   = profile.architecture
    val rates          = architecture.throughput
    val latency        = architecture.latency
    val sectorSource   = sectorSourceOf(architecture)
    val conflictSource = conflictSourceOf(architecture)
    val limitSources   =
      limitSourcesOf(architecture.limits, profile.occupancy.fold(Set.empty[Occupancy.Limiter])(_.limiters))
    val resident = profile.occupancy.fold(0)(_.subgroups)
    profile.instructions.collect {
      case (instruction, value) if value.issues > 0L =>
        val memory                    = value.memory.filterNot(_._1._1 == Space.Constant)
        val issues                    = value.issues.toDouble
        val lanes                     = value.activeLanes.toDouble
        val sectors                   = memory.values.map(_.sectors).sum.toDouble
        val wavefronts                = memory.values.map(_.wavefronts).sum.toDouble
        val loads                     = if (memory.keys.exists(_._2 == AccessKind.Read)) issues else 0.0
        val touchesShared             = memory.keys.exists(_._1 == Space.Shared)
        val touchesGlobal             = memory.keys.exists(_._1 == Space.Global)
        val (globalTier, globalLevel) = latency.global
        val tiers                     = Vec(
          Option.when(touchesShared)(latency.shared),
          Option.when(touchesGlobal)(globalTier)
        ).flatten
        val terms = Vec(
          Some((Binding.Issue, issues / rates.issues.value, rates.issues.source)),
          instruction.pipe.flatMap { pipe =>
            val rate = rates.rate(pipe)
            term(Binding.Arithmetic(pipe), lanes, rate, rate.source)
          },
          term(Binding.LoadStore, sectors, rates.loadStore, sectorSource),
          term(Binding.Shared, wavefronts, rates.shared, conflictSource),
          rates.loads.flatMap(rate => term(Binding.Loads, loads, rate, rate.source))
        ).flatten
        val pathWork               = value.plannedCycles.toDouble + issues * tiers.map(_.value).maxOption.getOrElse(0.0)
        val path                   = Option.when(instruction.schedule.nonEmpty && resident > 0)(pathWork / resident)
        val (topBinding, bound, _) = terms.maxBy(_._2)
        val binds                  = if (path.exists(_ > bound)) Binding.Path else topBinding
        val cycles                 = path.fold(bound)(_.max(bound))
        val scheduler              = instruction.schedule.collect { case Schedule.Estimated(_) =>
          Architecture.Source.Estimate(modelledPlanning._2)
        }
        val pathSources   = Option.when(path.exists(_ > 0.0))(tiers.map(_.source) ++ limitSources ++ scheduler)
        val sources       = terms.map(_._3) ++ pathSources.getOrElse(Vec.empty[Architecture.Source])
        val floor         = Option.when(path.isDefined && touchesGlobal)(globalLevel)
        val globalSectors = memory.collect { case ((Space.Global, _), access) => access.sectors }.sum.toDouble
        val dramIfMissed  = rates.dram.filter(_ => globalSectors > 0.0).map { rate =>
          globalSectors * architecture.global.sectorBytes.value / rate.value
        }
        Cost(cycles, bound, binds, path, floor, sources.max, instruction, dramIfMissed)
    }
  }

  private def located(instruction: ProfileStream.Instruction): String =
    instruction.function.fold("")(name => s"${name.raw} ") +
      instruction.address.fold("")(address => s"0x${address.raw.toHexString} ") + instruction.text

  private def placement(instruction: ProfileStream.Instruction): (Option[String], Option[Long], String) =
    (instruction.function.map(_.raw), instruction.address.map(_.raw), instruction.text)

  private def tierPhrase(tier: Tier): String = tier match {
    case Tier.L1   => "an L1 hit"
    case Tier.L2   => "an L2 hit"
    case Tier.Dram => "a DRAM access"
  }

  private def pathText(cost: Cost): String = (cost.path, cost.floor) match {
    case (None, _)                => "path unavailable"
    case (Some(path), None)       => f"path $path%.1f"
    case (Some(path), Some(tier)) => f"path $path%.1f (floor, assumes ${tierPhrase(tier)})"
  }

  private def costLine(cost: Cost): String =
    f"  ${cost.cycles}%.1f cycles: binds ${bindingName(cost.binds)}, ${pathText(cost)}, bound ${cost.bound}%.1f" +
      cost.dramIfMissed.fold("")(cycles => f", DRAM $cycles%.1f if every global sector misses") +
      s" [${cost.source.kind}] at " + located(cost.instruction)

  def summary(profile: Profile, top: Int): String = {
    val total = profile.total
    val lines = Vec.newBuilder[String]
    lines += s"architecture ${profile.architecture.name}"
    profile.occupancy.foreach { occupancy =>
      val limiters = occupancy.limiters.toVector.sortBy(_.ordinal).map(_.toString.toLowerCaseRoot).mkString(", ")
      lines += f"occupancy ${occupancy.fraction * 100.0}%.1f%%: ${occupancy.workgroups} workgroups, " +
        s"${occupancy.subgroups} of ${occupancy.maxSubgroups} subgroups, limited by $limiters"
    }
    profile.pressure.foreach(pressure =>
      lines += f"register pressure: peak ${pressure.peak}, mean ${pressure.mean}%.1f live 32-bit registers per subgroup"
    )
    lines += f"subgroup issues ${total.issues}%d, lane efficiency ${total.laneEfficiency * 100.0}%.1f%%"
    lines += s"divergent issues ${total.divergent}, reconverging issues ${total.reconverging}"
    if (scheduled(profile)) {
      val sources = planning(profile).map(_._2).mkString(" and ")
      lines += s"planned cycles ${total.plannedCycles}, from $sources, not measured time"
    }
    ordered(total).foreach { case ((space, kind), m) =>
      val detail =
        if (m.wavefronts > 0L) s"${m.wavefronts} wavefronts, ${m.conflicts} from bank conflicts"
        else f"${m.sectors} sectors (${m.sectors.toDouble / m.requests.max(1L).toDouble}%.1f per request), " +
          s"${m.idealSectors} ideal"
      lines += s"$space $kind: ${m.requests} requests, ${m.bytes} bytes, $detail"
    }
    val ranked = findings(profile)
    totals(ranked).foreach(total =>
      lines += f"waste ${total.what}: ${total.count} ${total.unit}, ${total.cycles}%.1f cycles of throughput " +
        s"[${total.source.kind}]"
    )
    if (ranked.nonEmpty) lines += "top findings by estimated cycles:"
    ranked.take(top).foreach(finding =>
      lines += f"  ${finding.cycles}%.1f cycles: ${finding.count} ${finding.unit} (${finding.what}) " +
        s"[${finding.source.kind}] at " + located(finding.instruction)
    )
    val priced = costs(profile).toVector.sortBy(cost => (-cost.cycles, placement(cost.instruction)))
    if (priced.nonEmpty) lines += "top instructions by estimated cycles:"
    priced.take(top).foreach(cost => lines += costLine(cost))
    val planned = profile.instructions.toVector
      .filter((_, value) => value.plannedCycles > 0L)
      .sortBy((instruction, value) => (-value.plannedCycles, placement(instruction)))
    if (planned.nonEmpty) lines += "top instructions by planned cycles:"
    planned.take(top).foreach { case (instruction, value) =>
      lines += s"  ${value.plannedCycles} planned cycles over ${value.issues} issues at " + located(instruction)
    }
    lines.result().mkString("\n")
  }
}
