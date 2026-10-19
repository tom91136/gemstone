package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*

extension (model: Model) {

  /** The definitions of `form`'s instances; empty where it has none. */
  def describe(entry: View.Entry, form: View.Form): Vec[Described] = model.semantics(entry, form).getOrElse(Vec.empty)

  /** Why `form` has no definition; the form must have none. */
  def unavailable(entry: View.Entry, form: View.Form): Unavailable =
    model.semantics(
      entry,
      form
    ).swap.getOrElse(throw IllegalStateException(s"${entry.mnemonic} ${form.name} is described"))
}
