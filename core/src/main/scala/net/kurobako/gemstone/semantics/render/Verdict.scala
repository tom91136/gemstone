package net.kurobako.gemstone.semantics.render

/** Equivalence outcome; `Different` carries a model of inputs that tell the definitions apart. */
enum Verdict derives CanEqual {
  case Equal
  case Different(counterexample: String)
  case Undecided(reason: String)
  case Unavailable
}
