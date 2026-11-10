package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.BlockAnalysis.*
import net.kurobako.gemstone.profile.PipelineSimulator.*
import net.kurobako.gemstone.profile.ProfileReport.*

object BlockAnalysisReport {
  private val MaxCycles = 64

  def text(regions: Vec[Region]): String =
    regions.map(region => regionLines(region).mkString("\n")).mkString("\n\n")

  def json(regions: Vec[Region]): ujson.Value = ujson.Arr.from(regions.map(regionJson(_)))

  private def regionLines(region: Region): Vec[String] = {
    val heading = Vec(s"${region.label} (${region.kind.name})")
    region.status match {
      case Status.NotExecuted      => heading ++ Vec("  status: not executed") ++ instructions(region.members)
      case Status.Withheld(reason) => heading ++ Vec(s"  status: withheld, $reason") ++ instructions(region.members)
      case Status.Executed(timing) => heading ++ executedLines(region.members, timing)
    }
  }

  private def instructions(members: Vec[Member]): Vec[String] =
    Vec("  instructions:") ++ members.map { member =>
      s"    ${hex(member.address)} ${member.issues} issues  ${member.text.getOrElse("unknown")}"
    }

  def bottleneck(block: PipelineSimulator.Block): String = {
    val (busiest, peak) = block.pressure.maxBy(_._2)
    val peakText        = f"$peak%.2f"
    if (block.binding == ProfileReport.Binding.Path)
      s"path, no resource saturated, busiest is ${bindingName(busiest)} at $peakText"
    else s"${bindingName(block.binding)} at $peakText"
  }

  def unknownPipeLine(timing: Timing): Option[String] =
    Option.when(timing.unknownPipes.nonEmpty) {
      s"unknown pipe: ${timing.unknownPipes.map(hex).mkString(", ")}, simulated as issue only"
    }

  def timelineSpan(block: PipelineSimulator.Block): String = {
    val last = firstIteration(block).map(_.retire).max
    if (last > MaxCycles) s"first $MaxCycles of $last cycles" else s"$last cycles"
  }

  def timelineBar(entry: PipelineSimulator.Entry): String = {
    val end = math.min(entry.retire, MaxCycles.toLong).toInt
    (0 until end).map { cell =>
      val cycle = cell.toLong
      if (cycle < entry.dispatch) ' ' else if (cycle < entry.issue) '.' else '='
    }.mkString
  }

  private def executedLines(members: Vec[Member], timing: Timing): Vec[String] = {
    val block   = timing.block
    val cycles  = f"  cycles per iteration: ${block.cyclesPerIteration}%.2f"
    val unknown = unknownPipeLine(timing).fold(Vec.empty[String])(line => Vec(s"  $line"))
    Vec(
      "  status: executed",
      cycles,
      s"  source: ${block.source.kind} (${block.source.detail})",
      s"  bottleneck: ${bottleneck(block)}"
    ) ++ unknown ++ timelineLines(members, block) ++ pressureLines(block)
  }

  private def timelineLines(members: Vec[Member], block: PipelineSimulator.Block): Vec[String] = {
    val rows = firstIteration(block).map { entry =>
      val member   = members(entry.index)
      val position = s"${entry.index.toString.padTo(3, ' ')} ${hex(member.address).padTo(6, ' ')}"
      s"    $position |${timelineBar(entry)}| ${member.text.getOrElse("unknown")}"
    }
    Vec(s"  timeline (warp 0, iteration 0, ${timelineSpan(block)}):") ++ rows
  }

  private def pressureLines(block: PipelineSimulator.Block): Vec[String] = {
    val width = block.pressure.map { case (binding, _) => bindingName(binding).length }.max
    Vec("  resource pressure:") ++ block.pressure.map { case (binding, pressure) =>
      val value = f"$pressure%.2f"
      s"    ${bindingName(binding).padTo(width, ' ')} $value"
    }
  }

  def firstIteration(block: PipelineSimulator.Block): Vec[PipelineSimulator.Entry] =
    block.entries.filter(entry => entry.warp == 0 && entry.iteration == 0L)

  private def regionJson(region: Region): ujson.Obj = {
    val obj = ujson.Obj(
      "label"        -> region.label,
      "kind"         -> region.kind.name,
      "kernel"       -> region.kernel,
      "status"       -> statusName(region.status),
      "instructions" -> ujson.Arr.from(region.members.map(memberJson(_)))
    )
    region.status match {
      case Status.NotExecuted      => ()
      case Status.Withheld(reason) => obj("reason") = reason
      case Status.Executed(timing) => executedJson(obj, region.members, timing)
    }
    obj
  }

  private def statusName(status: Status): String = status match {
    case Status.NotExecuted => "not executed"
    case Status.Withheld(_) => "withheld"
    case Status.Executed(_) => "executed"
  }

  private def memberJson(member: Member): ujson.Obj = {
    val obj = ujson.Obj("address" -> hex(member.address), "issues" -> ujson.Num(member.issues.toDouble))
    member.text.foreach(text => obj("text") = text)
    obj
  }

  private def executedJson(obj: ujson.Obj, members: Vec[Member], timing: Timing): Unit = {
    val block = timing.block
    obj("cyclesPerIteration") = ujson.Num(block.cyclesPerIteration)
    obj("binding") = bindingName(block.binding)
    obj("source") = ujson.Obj("kind" -> block.source.kind, "detail" -> block.source.detail)
    obj("pressure") = ujson.Arr.from(block.pressure.map { case (binding, pressure) =>
      ujson.Obj("resource" -> bindingName(binding), "pressure" -> ujson.Num(pressure))
    })
    obj("timeline") = ujson.Arr.from(firstIteration(block).map { entry =>
      ujson.Obj(
        "index"    -> ujson.Num(entry.index.toDouble),
        "address"  -> hex(members(entry.index).address),
        "dispatch" -> ujson.Num(entry.dispatch.toDouble),
        "issue"    -> ujson.Num(entry.issue.toDouble),
        "retire"   -> ujson.Num(entry.retire.toDouble),
        "stalls"   -> ujson.Obj(
          "dependency" -> ujson.Num(entry.stalls.getOrElse(Stall.Dependency, 0L).toDouble),
          "pipeBusy"   -> ujson.Num(entry.stalls.getOrElse(Stall.PipeBusy, 0L).toDouble),
          "issueSlot"  -> ujson.Num(entry.stalls.getOrElse(Stall.IssueSlot, 0L).toDouble)
        )
      )
    })
    obj("unknownPipes") = ujson.Arr.from(timing.unknownPipes.map(address => ujson.Str(hex(address))))
  }
}
