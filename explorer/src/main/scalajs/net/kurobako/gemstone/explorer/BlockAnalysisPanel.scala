package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*

object BlockAnalysisPanel {

  def render(model: BlockAnalysisView.Model): HtmlElement = model match {
    case BlockAnalysisView.Model.Unavailable(reason) =>
      div(cls := "blocks-summary", h4("Blocks"), p(cls := "empty", s"analysis unavailable: $reason"))
    case BlockAnalysisView.Model.Analysed(regions) =>
      div(cls := "blocks-summary", h4("Blocks and loops"), regions.map(region(_)))
  }

  private def region(found: BlockAnalysisView.Region): HtmlElement =
    div(
      cls := "block-region",
      div(cls := "block-head", span(cls := "badge", found.kind.name), code(found.label)),
      found.state match {
        case BlockAnalysisView.State.NotExecuted       => unexecuted("not executed", found.members)
        case BlockAnalysisView.State.Withheld(reason)  => unexecuted(s"withheld, $reason", found.members)
        case BlockAnalysisView.State.Executed(summary) => executed(summary)
      }
    )

  private def unexecuted(status: String, members: Vec[BlockAnalysis.Member]): HtmlElement =
    div(
      p(cls   := "block-status", status),
      div(cls := "block-instructions", members.map(instruction(_)))
    )

  private def instruction(member: BlockAnalysis.Member): HtmlElement =
    div(
      cls := "block-row",
      span(cls := "address", BlockAnalysis.hex(member.address)),
      span(cls := "issues", s"${member.issues} issues"),
      span(cls := "text", member.text.getOrElse("unknown"))
    )

  private def executed(summary: BlockAnalysisView.Summary): HtmlElement =
    div(
      p(cls := "block-status", "executed"),
      p(f"${summary.cyclesPerIteration}%.2f cycles per iteration"),
      provenance(summary.source),
      p(s"bottleneck: ${summary.bottleneck}"),
      summary.unknownPipe.map(line => p(cls := "caveat", line)),
      h5(s"timeline (warp 0, iteration 0, ${summary.timelineSpan})"),
      div(
        cls := "block-instructions",
        summary.timeline.map(row =>
          div(
            cls := "block-row",
            span(cls := "address", s"${row.index} ${row.address}"),
            span(cls := "bar", row.bar),
            span(cls := "text", row.text)
          )
        )
      ),
      h5("resource pressure"),
      table(
        cls := "pressure",
        tbody(summary.pressure.map { (name, pressure) =>
          tr(th(name), td(f"$pressure%.2f"))
        })
      )
    )

  private def provenance(source: Architecture.Source): HtmlElement = source match {
    case Architecture.Source.Estimate(basis) =>
      p(cls := "source source-estimate", s"source: estimate, not a measurement ($basis)")
    case _ => p(cls := "source", s"source: ${source.kind} (${source.detail})")
  }
}
