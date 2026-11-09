package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.profile.*

/** Controls, legend and summary for the heat map over the program listing: which metric shades the lines, the
  * architecture whose models measure the launch, and what the profile found.
  */
object HeatView {

  def controls(enabled: Var[Boolean], metric: Var[Heat.Metric], architecture: Var[Option[Architecture]]): HtmlElement =
    div(
      cls := "heat-controls",
      label(
        cls := "field",
        input(typ := "checkbox", checked <-- enabled.signal, onChange.mapToChecked --> enabled),
        "heat map"
      ),
      select(
        disabled <-- enabled.signal.map(!_),
        onChange.mapToValue.map(name => Heat.Metric.values.find(_.title == name).get) --> metric,
        Heat.Metric.values.toVec.map(each =>
          option(value := each.title, each.title, selected <-- metric.signal.map(_ == each))
        )
      ),
      select(
        title := "the architecture whose memory and throughput models measure the launch",
        onChange.mapToValue.map(name => Architecture.named(name)) --> architecture,
        option(
          value    := "",
          disabled := true,
          "choose an architecture",
          selected <-- architecture.signal.map(_.isEmpty)
        ),
        Architecture.all.map(each =>
          option(value := each.name, each.name, selected <-- architecture.signal.map(_.contains(each)))
        )
      )
    )

  def legend(metric: Heat.Metric, maximum: Double): HtmlElement =
    div(
      cls := "heat-legend",
      span(cls := "heat-legend-label", s"${metric.unit}, 0 is unshaded"),
      Heat.bounds(maximum).zipWithIndex.map { (bound, index) =>
        span(
          cls   := s"heat-swatch heat-${index + 1}",
          title := s"up to ${Heat.format(bound)} ${metric.unit}",
          Heat.format(bound)
        )
      }
    )

  def choose: HtmlElement = p(cls := "empty", "Choose an architecture to measure this launch.")

  /** The launch's totals, its occupancy and the findings that cost the most, each named by its listing line. */
  def summary(profile: Profile, found: Vec[ProfileReport.Finding], listing: Option[Listing]): HtmlElement = {
    val total = profile.total
    div(
      cls := "heat-summary",
      h4("Profile"),
      p(
        s"${total.issues} subgroup issues, lane efficiency ${Heat.format(total.laneEfficiency * 100.0)}%, " +
          s"${total.divergent} divergent"
      ),
      Heat.occupancy(profile).map(text => p(text)),
      if (found.isEmpty) p(cls := "empty", "No waste found.")
      else
        ol(
          cls := "heat-findings",
          found.map { finding =>
            val line = listing.flatMap(Heat.lineOf(_, finding.instruction))
            li(
              span(cls   := "heat-cycles", s"${Heat.format(finding.cycles)} cycles"),
              span(title := finding.source.detail, s" [${finding.source.kind}]"),
              s" ${finding.count} ${finding.unit} (${finding.what}) at ",
              code(line.fold(finding.instruction.text)(_.text))
            )
          }
        )
    )
  }
}
