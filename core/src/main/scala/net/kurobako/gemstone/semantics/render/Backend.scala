package net.kurobako.gemstone.semantics.render

/** A language definitions are written out in, with its own spelling of their types, names and terms. */
trait Backend {

  /** `definition` alone. */
  def show(definition: Definition): String

  /** `definition` with what it refers to brought into scope, as a source of its own. */
  def source(definition: Definition): String
}
