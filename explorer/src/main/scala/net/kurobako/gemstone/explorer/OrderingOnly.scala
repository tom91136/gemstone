package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import scala.util.matching.Regex

object OrderingOnly {
  def apply(rows: (String, String, String)*): OrderingOnly =
    new OrderingOnly(Vec.from(rows).map((mnemonic, form, reason) => (mnemonic.r, form.r, reason)))
}

/** Ordering forms the VM runs with no observable effect, as rows of mnemonic pattern, form pattern and reason. The
  * first row whose patterns both match a form gives its NoOp.
  */
class OrderingOnly private (rows: Vec[(Regex, Regex, String)]) {
  def semantics(mnemonic: String, form: String): Option[Semantics] = rows.collectFirst {
    case (mnemonics, forms, reason) if mnemonics.matches(mnemonic) && forms.matches(form) => Semantics.NoOp(reason)
  }
}
