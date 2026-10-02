package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.constraint.*

/** Shared checked boundary for vendor operand spellings. */
private object OperandDomains {
  def nonEmpty(value: String): NonEmpty[String] =
    value.refine[NonEmpty[String]].fold(error => throw IllegalArgumentException(error.raw), identity)
}
