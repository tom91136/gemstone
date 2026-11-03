package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchObserver.*

/** A [[Profile]] as JSON lines and as a text summary. The JSON schema is versioned by `schema`: one `launch` line, then
  * one `instruction` line for each static instruction that issued. Register pressure counts 32-bit register units, and
  * every line carries its `findings`: what it wastes, as a count in its own unit and as estimated cycles of the
  * resource it wastes, which the architecture's [[Architecture.Throughput]] bounds.
  */
object ProfileReport {
  val Schema: Int = 2

  private def ordered(counters: Profile.Counters): Vector[((Space, AccessKind), Profile.Memory)] =
    counters.memory.toVector.sortBy((key, _) => (key._1.ordinal, key._2.ordinal))

  private def ranked(found: Iterable[Finding]): Vec[Finding] =
    Vec.from(found.toVector.sortBy(finding => (-finding.cycles, finding.what)))

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

  private def counters(value: Profile.Counters): ujson.Obj = ujson.Obj(
    "issues"         -> value.issues.toDouble,
    "activeLanes"    -> value.activeLanes.toDouble,
    "subgroupLanes"  -> value.subgroupLanes.toDouble,
    "laneEfficiency" -> value.laneEfficiency,
    "divergent"      -> value.divergent.toDouble,
    "reconverging"   -> value.reconverging.toDouble,
    "memory"         -> memory(value)
  )

  private def findingJson(value: Finding): ujson.Obj = ujson.Obj(
    "what"   -> value.what,
    "count"  -> value.count.toDouble,
    "unit"   -> value.unit,
    "cycles" -> value.cycles
  )

  def jsonLines(profile: Profile): Vec[String] = {
    val ranked     = findings(profile)
    val byLocation = ranked.groupBy(_.instruction)
    val launch     = counters(profile.total)
    launch("record") = "launch"
    launch("schema") = Schema
    launch("architecture") = profile.architecture.name
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
        val line = counters(value)
        line("record") = "instruction"
        line("index") = index
        line("kernel") = instruction.kernel.raw
        instruction.function.foreach(name => line("function") = name.raw)
        instruction.address.foreach(address => line("address") = address.raw.toDouble)
        line("text") = instruction.text
        line("findings") = ujson.Arr.from(byLocation.getOrElse(instruction, Vec.empty[Finding]).map(findingJson(_)))
        line
    }
    (launch +: instructions).map(ujson.write(_))
  }

  /** What an instruction wastes, as `count` of `unit` and as the `cycles` of the resource that waste occupies. */
  case class Finding(
      cycles: Double,
      count: Long,
      unit: String,
      what: String,
      instruction: ProfileStream.Instruction
  ) derives CanEqual

  private def totals(found: Vec[Finding]): Vec[Finding] =
    ranked(found.groupBy(_.what).values.map { group =>
      group.head.copy(cycles = group.map(_.cycles).sum, count = group.map(_.count).sum)
    })

  /** What each instruction wastes, ranked together by the cycles it costs: sectors fetched beyond the ideal at the L1
    * sector rate, bank-conflict wavefronts at the shared wavefront rate, and the issue slots idle lanes waste at the
    * subgroup issue rate. The cycles are throughput bounds of one multiprocessor, not timing; the raw count and its
    * unit stay in the finding.
    */
  def findings(profile: Profile): Vec[Finding] = {
    val rates = profile.architecture.throughput
    val found = profile.instructions.flatMap { (instruction, value) =>
      val sectors   = value.memory.values.map(m => m.sectors - m.idealSectors).sum
      val conflicts = value.memory.values.map(_.conflicts).sum
      val idle      = value.subgroupLanes - value.activeLanes
      val idleSlots = value.issues.toDouble * (1.0 - value.laneEfficiency)
      Vec(
        Finding(sectors.toDouble / rates.sectorsPerCycle.value, sectors, "sectors", "excess sectors", instruction),
        Finding(
          conflicts.toDouble / rates.wavefrontsPerCycle.value,
          conflicts,
          "wavefronts",
          "bank-conflict wavefronts",
          instruction
        ),
        Finding(idleSlots / rates.issuesPerCycle.value, idle, "lanes", "idle lanes", instruction)
      ).filter(_.count > 0L)
    }
    ranked(found)
  }

  private def located(instruction: ProfileStream.Instruction): String =
    instruction.function.fold("")(name => s"${name.raw} ") +
      instruction.address.fold("")(address => s"0x${address.raw.toHexString} ") + instruction.text

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
    ordered(total).foreach { case ((space, kind), m) =>
      val detail =
        if (m.wavefronts > 0L) s"${m.wavefronts} wavefronts, ${m.conflicts} from bank conflicts"
        else f"${m.sectors} sectors (${m.sectors.toDouble / m.requests.max(1L).toDouble}%.1f per request), " +
          s"${m.idealSectors} ideal"
      lines += s"$space $kind: ${m.requests} requests, ${m.bytes} bytes, $detail"
    }
    val ranked = findings(profile)
    totals(ranked).foreach(total =>
      lines += f"waste ${total.what}: ${total.count} ${total.unit}, ${total.cycles}%.1f cycles of throughput"
    )
    if (ranked.nonEmpty) lines += "top findings by estimated cycles:"
    ranked.take(top).foreach(finding =>
      lines += f"  ${finding.cycles}%.1f cycles: ${finding.count} ${finding.unit} (${finding.what}) at " +
        located(finding.instruction)
    )
    lines.result().mkString("\n")
  }
}
