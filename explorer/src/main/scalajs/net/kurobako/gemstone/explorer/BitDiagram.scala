package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*

/** An instruction word drawn 32 bits per row, most significant row and bit first. */
object BitDiagram {
  private val RowBits = 32

  private def colour(index: Int): String = s"hsl(${index * 47 % 360} 65% 85%)"

  def apply(encoding: View.Encoding): HtmlElement = {
    val owners = encoding.fields.zipWithIndex.flatMap((field, index) => field.positions.map(_ -> index)).toMap
    val rows   = (encoding.width + RowBits - 1) / RowBits
    def row(start: Int): Vec[HtmlElement] = {
      val bits      = Vec.from((start + RowBits - 1 to start by -1).filter(_ < encoding.width))
      val bitOwners = bits.map(owners.get)
      val starts    = bits.indices.filter(at => at == 0 || bitOwners(at) != bitOwners(at - 1))
      val runs      = Vec.from(starts.zip(starts.drop(1) :+ bits.length).map((from, until) =>
        bitOwners(from) -> bits.slice(from, until)
      ))
      Vec(
        tr(cls := "bit-numbers", bits.map(bit => td(bit.toString))),
        tr(
          cls := "bit-fields",
          runs.map {
            case (Some(index), members) =>
              val field = encoding.fields(index)
              td(
                colSpan   := members.size,
                styleAttr := (if (field.ignored) "" else s"background: ${colour(index)}"),
                cls       := (if (field.ignored) "bit-ignored" else ""),
                title     := (if (field.ignored) "not read by this opcode" else DomainView.tooltip(field)),
                if (field.name.length <= members.size * 3) field.name else "..."
              )
            case (None, members) => td(colSpan := members.size, cls := "bit-unassigned")
          }
        ),
        tr(
          cls := "bit-values",
          bits.map(bit => td(encoding.fixed.get(bit).fold("")(value => if (value) "1" else "0")))
        )
      )
    }
    div(
      cls := "bit-diagram",
      table((rows - 1 to 0 by -1).flatMap(index => row(index * RowBits))),
      div(
        cls := "bit-legend",
        encoding.fields.zipWithIndex.map((field, index) =>
          span(cls := "bit-key", styleAttr := s"background: ${colour(index)}", title := field.detail, field.name)
        )
      ),
      div(
        cls := "bit-domains",
        encoding.fields.flatMap(field =>
          if (field.ignored)
            Some(div(cls := "bit-domain bit-domain-ignored", s"${field.detail}: not read by this opcode"))
          else field.domain.map(domain => DomainView.field(field.detail, domain))
        )
      )
    )
  }
}
