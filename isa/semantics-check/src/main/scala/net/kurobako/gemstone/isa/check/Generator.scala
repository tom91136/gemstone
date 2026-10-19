package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*

/** One ISA's generated sources. */
trait Generator {
  def sources: Vec[(name: String, source: String)]
}
