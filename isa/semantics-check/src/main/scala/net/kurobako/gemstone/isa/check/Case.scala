package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.semantics.render.*

/** A definition to check against executing `instance` on `target`, with what a reference needs to find the instance. */
case class Case(instance: String, target: Option[String], definition: Definition, lookup: Lookup)
