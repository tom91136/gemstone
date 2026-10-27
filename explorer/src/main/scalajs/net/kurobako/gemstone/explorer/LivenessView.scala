package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*

/** Draws register liveness over the issue stream: one row per register, its live spans shaded by how many units kept it
  * live there, and the live-register total of the resident units underneath. Clicking a span or a bar selects the issue
  * it starts at.
  */
object LivenessView {

  /** How many bars the widest histogram uses; a trace with fewer issues gets one bar per issue. */
  private val Columns = 480

  def render(trace: Trace, liveness: Liveness)(seek: Long => Unit): HtmlElement =
    if (!liveness.reported || liveness.isEmpty)
      div(
        cls := "liveness unavailable",
        span(cls := "liveness-label", "live registers"),
        span(cls := "liveness-note", "this ISA's view reports no register uses")
      )
    else {
      val duration  = math.max(1L, trace.issues)
      val functions = liveness.registers.map(_.function).distinct
      val rows      = liveness.registers.map(row => register(row, duration, functions.size > 1)(seek))
      val count     = math.min(Columns, trace.steps.size)
      val extent    = trace.steps.size
      val bars      = Vec.tabulate(count) { column =>
        val from   = (column.toLong * extent / count).toInt
        val to     = math.max(from + 1, ((column + 1).toLong * extent / count).toInt)
        val peak   = liveness.pressure.slice(from, to).max
        val issue  = trace.steps(from).issue
        val last   = trace.steps(to - 1).issue
        val height = if (liveness.peak <= 0) 0.0 else peak.toDouble / liveness.peak.toDouble * 100.0
        span(
          cls       := "liveness-bar",
          styleAttr := f"height:$height%.2f%%",
          title     := s"up to $peak live registers over issues $issue to $last",
          onClick --> (_ => seek(issue))
        )
      }
      div(
        cls := "liveness",
        div(
          cls := "liveness-head",
          span(cls := "liveness-label", "register liveness"),
          span(cls := "liveness-peak", s"${liveness.registers.size} registers")
        ),
        if (liveness.registers.isEmpty) div(cls := "empty", "No register is read along the recorded path.")
        else div(cls                            := "register-rows", rows),
        div(
          cls := "liveness-head histogram-head",
          span(cls := "liveness-label", "live registers (per lane, all units)"),
          span(cls := "liveness-peak", s"peak ${liveness.peak}")
        ),
        div(
          cls := "liveness-histogram",
          span(cls := "histogram-label", "live"),
          div(cls  := "liveness-bars", bars)
        )
      )
    }

  /** One register's row: its live spans over the same issue axis the occupancy tracks use. */
  private def register(row: RegisterTimeline, duration: Long, qualify: Boolean)(seek: Long => Unit): HtmlElement = {
    val most  = math.max(1, row.spans.map(_.units).maxOption.getOrElse(1))
    val label = if (qualify && row.function.nonEmpty) s"${row.function}:${row.name}" else row.name
    div(
      cls := "register-row",
      span(cls := "register-name", title := row.function, label),
      div(
        cls := "register-track",
        row.spans.map { part =>
          val left    = part.start.toDouble / duration.toDouble * 100.0
          val width   = math.max((part.end - part.start).toDouble / duration.toDouble * 100.0, 0.06)
          val opacity = 0.35 + 0.65 * part.units.toDouble / most.toDouble
          span(
            cls       := "register-span",
            styleAttr := f"left:$left%.4f%%;width:$width%.4f%%;opacity:$opacity%.2f",
            title     :=
              s"${part.units} unit${if (part.units == 1) "" else "s"} live over issues ${part.start} to ${part.end - 1L}",
            onClick --> (_ => seek(part.start))
          )
        }
      )
    )
  }
}
