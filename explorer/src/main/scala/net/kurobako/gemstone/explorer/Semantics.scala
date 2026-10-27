package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.Unavailable as UnavailableReason

object Semantics {

  /** The result for a form from its lane definitions and its machine definitions, either of which may be empty. */
  def of(lane: Either[UnavailableReason, Vec[Described]], machine: Vec[Described]): Semantics = lane match {
    case Right(descriptions) => if (machine.isEmpty) Lane(descriptions) else Both(descriptions, machine)
    case Left(reason)        => if (machine.isEmpty) Semantics.Unavailable(reason) else Machine(machine)
  }
}

/** What an instruction form contributes to the explorer: lane values, machine operations, both, or neither.
  *
  * `Lane` keeps the existing per-lane executable definitions. `Machine` has one definition per machine operation the
  * instances of the form lower to. `Both` is a form whose instances split between the two. `NoOp` is an ordering form
  * the VM runs with no effect it can observe, with the reason why. `LoadTime` is a declaration the VM reads as it loads
  * a module, which no invocation runs. `Unavailable` carries the existing reason an ISA could not describe the form.
  */
enum Semantics {
  case Lane(descriptions: Vec[Described])
  case Machine(definitions: Vec[Described])
  case Both(lane: Vec[Described], machine: Vec[Described])
  case NoOp(reason: String)
  case LoadTime(reason: String)
  case Unavailable(reason: UnavailableReason)
}
