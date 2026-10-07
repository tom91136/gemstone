package net.kurobako.gemstone.isa

import net.kurobako.gemstone.constraint.*

/** A program global that shares the storage of another symbol. */
case class ProgramGlobalAlias(name: NonEmpty[String], offset: Long >= 0)
