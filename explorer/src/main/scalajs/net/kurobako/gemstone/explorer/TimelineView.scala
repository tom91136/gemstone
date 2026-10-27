package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

/** Draws a trace as occupancy over its issue stream: one row per workgroup, one band per unit, and a band's height the
  * lanes it carries. Clicking a stretch selects the issue it starts at.
  */
object TimelineView {

  /** The height one lane of a unit takes in its row, and the most a row may take before its lanes are drawn shorter. */
  private val LaneHeight    = 9
  private val MaximumHeight = 160

  private def laneHeight(lanes: Int): Int =
    math.max(1, math.min(LaneHeight, MaximumHeight / math.max(1, lanes)))

  def render(trace: Trace, rows: Vec[Row])(seek: Long => Unit): HtmlElement = {
    val duration = math.max(1L, trace.issues)
    div(cls := "timeline", rows.map(row => this.row(row, duration)(seek)))
  }

  private def row(row: Row, duration: Long)(seek: Long => Unit): HtmlElement = {
    val laneHeight = this.laneHeight(row.lanes)
    val height     = math.max(1, row.lanes) * laneHeight
    val tops       = row.bands.scanLeft(0)(_ + _.unit.lanes.raw)
    val bands      = row.bands.zip(tops).map { (band, lanes) =>
      val top    = lanes * laneHeight
      val extent = math.max(laneHeight, band.unit.lanes.raw * laneHeight)
      div(
        cls       := "band",
        title     := s"unit ${band.unit.ordinal.raw} - ${band.unit.lanes.raw} lanes",
        styleAttr := s"top:${top}px;height:${extent}px",
        band.segments.map(segment => this.segment(segment, duration)(seek))
      )
    }
    div(
      cls := "row",
      div(
        cls   := "row-label",
        title := s"workgroup (${row.workgroup.x.raw}, ${row.workgroup.y.raw}, ${row.workgroup.z.raw})",
        s"(${row.workgroup.x.raw}, ${row.workgroup.y.raw}, ${row.workgroup.z.raw})",
        span(cls := "row-lanes", s"${row.lanes} lanes")
      ),
      div(cls := "track", styleAttr := s"height:${height}px", bands)
    )
  }

  private def segment(segment: Segment, duration: Long)(seek: Long => Unit): HtmlElement = {
    val left  = segment.start.toDouble / duration.toDouble * 100.0
    val width = math.max(segment.width.toDouble / duration.toDouble * 100.0, 0.08)
    div(
      cls       := s"segment ${classes(segment.state)}",
      styleAttr := f"left:$left%.4f%%;width:$width%.4f%%",
      title     := s"${label(segment.state)} - issues ${segment.start} to ${segment.end}",
      onClick --> (_ => seek(segment.start))
    )
  }

  /** What a state is called, with the reason a unit waits. */
  def label(state: MachineUnit.State): String = state match {
    case MachineUnit.State.Running         => "running"
    case MachineUnit.State.Retired         => "finished"
    case MachineUnit.State.Blocked(reason) => s"waiting for ${describe(reason)}"
  }

  /** The reason and the name it waits on. */
  def describe(reason: MachineUnit.Reason): String = reason match {
    case MachineUnit.Reason.Barrier(id)    => id
    case MachineUnit.Reason.Memory         => "memory"
    case MachineUnit.Reason.Members        => "its own lanes"
    case MachineUnit.Reason.Collective(id) => id
    case MachineUnit.Reason.Cluster(id)    => s"cluster $id"
  }

  private def classes(state: MachineUnit.State): String = state match {
    case MachineUnit.State.Running         => "running"
    case MachineUnit.State.Retired         => "retired"
    case MachineUnit.State.Blocked(reason) => "blocked " +
        (reason match {
          case MachineUnit.Reason.Barrier(_)    => "barrier"
          case MachineUnit.Reason.Memory        => "memory"
          case MachineUnit.Reason.Members       => "members"
          case MachineUnit.Reason.Collective(_) => "collective"
          case MachineUnit.Reason.Cluster(_)    => "cluster"
        })
  }
}
