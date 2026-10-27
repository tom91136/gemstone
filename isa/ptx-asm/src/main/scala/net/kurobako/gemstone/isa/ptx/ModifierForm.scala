package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

object ModifierForm {

  /** The one value every alternative gives, when they agree. */
  def agreed[A](values: Seq[A]): Option[A] = values.distinct match {
    case Seq(one) => Some(one)
    case _        => None
  }
}

/** One alternative of an instruction's syntax: the slots its modifiers fill, in order, and its operands. */
case class ModifierForm(
    required: Vec[ModifierDomain],
    optional: Vec[ModifierDomain],
    unordered: Set[Modifier],
    complete: Boolean,
    operands: Vec[OperandSyntax]
) {

  /** The names of `count` operands, where every way of choosing the optional operands to include agrees. */
  def names(count: Int): Vec[Option[String]] = {
    val required = operands.count(!_.optional)
    val choices  = operands.indices.filter(operands(_).optional).toVec.combinations(count - required).toVec
    val chosen   = choices.map(_.toSet).map(picked =>
      operands.indices.view.filter(index => !operands(index).optional || picked(index)).map(operands(_).name).toVec
    )
    if (count < required || chosen.isEmpty) Vec.fill(count)(None)
    else Vec.tabulate(count)(position => ModifierForm.agreed(chosen.map(_(position))))
  }

  def accepts(count: Int): Boolean = count >= operands.count(!_.optional) && count <= operands.size
}
