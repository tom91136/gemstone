package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*

object BlockAnalysisView {

  enum Model derives CanEqual {
    case Unavailable(reason: String)
    case Analysed(regions: Vec[Region])
  }

  enum State derives CanEqual {
    case NotExecuted
    case Withheld(reason: String)
    case Executed(summary: Summary)
  }

  case class Region(label: String, kind: BlockAnalysis.Kind, members: Vec[BlockAnalysis.Member], state: State)

  case class Summary(
      cyclesPerIteration: Double,
      source: Architecture.Source,
      bottleneck: String,
      pressure: Vec[(String, Double)],
      timelineSpan: String,
      timeline: Vec[TimelineRow],
      unknownPipe: Option[String]
  )

  case class TimelineRow(index: Int, address: String, text: String, bar: String)

  def model(stream: ProfileStream, architecture: Architecture): Model =
    BlockAnalysis.of(stream, architecture) match {
      case Left(reason)    => Model.Unavailable(reason)
      case Right(analysis) => Model.Analysed(analysis.regions.map(region(_)))
    }

  private def region(found: BlockAnalysis.Region): Region =
    Region(found.label, found.kind, found.members, state(found.members, found.status))

  private def state(members: Vec[BlockAnalysis.Member], status: BlockAnalysis.Status): State = status match {
    case BlockAnalysis.Status.NotExecuted      => State.NotExecuted
    case BlockAnalysis.Status.Withheld(reason) => State.Withheld(reason)
    case BlockAnalysis.Status.Executed(timing) => State.Executed(summary(members, timing))
  }

  private def summary(members: Vec[BlockAnalysis.Member], timing: BlockAnalysis.Timing): Summary = {
    val block = timing.block
    Summary(
      cyclesPerIteration = block.cyclesPerIteration,
      source = block.source,
      bottleneck = BlockAnalysisReport.bottleneck(block),
      pressure = block.pressure.map { case (binding, pressure) => ProfileReport.bindingName(binding) -> pressure },
      timelineSpan = BlockAnalysisReport.timelineSpan(block),
      timeline = BlockAnalysisReport.firstIteration(block).map { entry =>
        val member = members(entry.index)
        TimelineRow(
          entry.index,
          BlockAnalysis.hex(member.address),
          member.text.getOrElse("unknown"),
          BlockAnalysisReport.timelineBar(entry)
        )
      },
      unknownPipe = BlockAnalysisReport.unknownPipeLine(timing)
    )
  }
}
