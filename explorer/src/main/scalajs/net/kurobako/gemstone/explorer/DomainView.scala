package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*

/** An operand field's accepted values: the summary line with the full list of values and codes under it. */
object DomainView {

  def tooltip(field: View.Field): String =
    field.domain.fold(field.detail)(domain => s"${field.detail}: ${domain.summary}")

  private def values(domain: View.Domain): HtmlElement =
    table(
      cls := "domain-values",
      thead(tr(th("Code"), th("Value"))),
      tbody(domain.values.map((code, meaning) => tr(td(code), td(meaning))))
    )

  private def expandable(heading: String, domain: View.Domain): HtmlElement =
    if (domain.values.isEmpty) div(cls := "domain-summary", heading)
    else detailsTag(cls                := "domain-summary", summaryTag(heading), values(domain))

  def field(detail: String, domain: View.Domain): HtmlElement =
    expandable(s"$detail: ${domain.summary}", domain).amend(cls := "bit-domain")

  def operand(domain: View.Domain): HtmlElement = expandable(domain.summary, domain)
}
