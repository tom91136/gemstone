package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*

/** What the explorer renders for one form, independent of Laminar. Lane and machine definitions each become a part, and
  * an unavailable result, an ordering form without effect or a load-time declaration keeps its reason.
  */
object SemanticsParts {

  enum Part {
    case Lane(descriptions: Vec[Described])
    case Machine(descriptions: Vec[Described])
    case UnavailableReason(reason: Unavailable)
    case NoOp(reason: String)
    case LoadTime(reason: String)
  }

  def parts(semantics: Semantics): Vec[Part] = semantics match {
    case Semantics.Lane(descriptions)   => Vec(Part.Lane(descriptions))
    case Semantics.Machine(definitions) => Vec(Part.Machine(definitions))
    case Semantics.Both(lane, machine)  => Vec(Part.Lane(lane), Part.Machine(machine))
    case Semantics.NoOp(reason)         => Vec(Part.NoOp(reason))
    case Semantics.LoadTime(reason)     => Vec(Part.LoadTime(reason))
    case Semantics.Unavailable(reason)  => Vec(Part.UnavailableReason(reason))
  }
}
