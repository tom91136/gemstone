package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** The lane masks of a lane definition: `wave.lane()` is the lane it describes, and `wave.ballot(p)` a mask that holds
  * `p` in that lane's bit and zero in every other, as a wave in which that lane alone is active writes it. A definition
  * that reaches a mask only through these and reads only its own bit of one, `(mask >> wave.lane()).to[UInt[1]]`,
  * depends on no other lane.
  */
object LaneMasks {

  /** The name of the lane as an input of a proof. */
  val Lane = "lane"

  val LaneType: Type = Type.unsigned(32)

  /** The lanes of the widest wave, which bound the lane of a proof on every target. */
  val Lanes = 64

  /** The type of the context a lane definition reaches its wave through. */
  val contextType: String = RenderContext.Wave.contextType

  private def wave(term: Term, method: String): Boolean = term match {
    case Term.Context(contextType = owner, method = called) => owner == contextType && called == method
    case _                                                  => false
  }

  /** Whether `term` is a call a proof over one lane can take. */
  def admitted(term: Term): Boolean = wave(term, "lane") || wave(term, "ballot")

  private def all(term: Term): Iterator[Term] = Iterator(term) ++ term.children.iterator.flatMap(all(_))

  def uses(term: Term): Boolean = all(term).exists(admitted(_))

  /** Whether `term` writes a mask of the lane's bit. */
  def ballots(term: Term): Boolean = all(term).exists(wave(_, "ballot"))

  /** `term` with the lane the variable [[Lane]] and each ballot the mask it stands for. */
  def plain(term: Term): Term = term match {
    case context: Term.Context if wave(context, "lane") => Term.Variable(Lane)
    case Term.Context(arguments = Vec(predicate), result = Some(Type.Integer(width, _)))
        if wave(term, "ballot") =>
      def mask(value: Long) = Term.Call(s"UInt[$width]", Vec(Term.Literal(value)))
      Term.Select(plain(predicate), Term.Binary("<<", mask(1L), Term.Variable(Lane)), mask(0L))
    case other => other.mapChildren(plain(_))
  }

  /** Whether `term` reads `name` other than as the lane's own bit of it. */
  def whole(term: Term, name: String): Boolean = term match {
    case Term.Call(".to[UInt[1]]", Vec(Term.Binary(">>", Term.Variable(`name`), lane))) if wave(lane, "lane") => false
    case Term.Variable(`name`)                                                                                => true
    case other => other.children.exists(whole(_, name))
  }
}
