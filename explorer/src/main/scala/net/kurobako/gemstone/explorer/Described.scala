package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.semantics.render.*

/** The semantics of `instance`, an instruction of the form, as an executable definition. */
case class Described(instance: String, definition: Definition)
