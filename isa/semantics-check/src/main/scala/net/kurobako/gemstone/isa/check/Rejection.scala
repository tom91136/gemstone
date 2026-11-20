package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.isa.amdgpu.LaneMasks
import net.kurobako.gemstone.isa.nvidia.Machine as NvidiaMachine
import net.kurobako.gemstone.semantics.render.*

/** Why an instruction cannot stand in an equivalence proof: it has no definition, its definition reaches state by
  * anything but a plain memory access, a read of a constant bank or the lane's own bit of a lane mask, or a
  * floating-point mode of it is not rendered.
  */
object Rejection {

  def of(kase: Case): Option[String] =
    if (undefined(kase)) Some(s"${kase.instance} has no definition")
    else unsupported(kase).orElse(Modes.check(kase).left.toOption)

  def undefined(kase: Case): Boolean = kase.definition.body.isEmpty && kase.definition.results.isEmpty

  def unsupported(kase: Case): Option[String] = {
    val definition = kase.definition
    val roots      = definition.body.iterator.map(_.term) ++ definition.requirements.iterator ++
      definition.results.iterator.map(_.value)
    roots.flatMap(terms(_)).flatMap(refusal(_)).nextOption().map(reason => s"${kase.instance} is refused: $reason")
  }

  def terms(term: Term): Iterator[Term] = Iterator(term) ++ term.children.iterator.flatMap(terms(_))

  /** A read of a constant bank, which no instruction writes. */
  private def constant(contextType: String, method: String): Boolean =
    contextType == NvidiaMachine.constantContext._1 && method == "load"

  private def refusal(term: Term): Option[String] = term match {
    case Term.Context(contextType = contextType, method = method, result = result, effect = effect) =>
      val owner   = contextType.substring(contextType.lastIndexOf('.') + 1)
      val machine = contextType == RenderMachine.Machine.contextType
      if (constant(contextType, method) || LaneMasks.admitted(term)) None
      else if (!machine || !Set("load", "store", "storeBytes", "alignLoad", "alignStore").contains(method))
        Some(s"$owner.$method is not a plain memory access")
      else if (effect && result.nonEmpty) Some(s"atomic update through $owner.$method")
      else None
    case _ => None
  }
}
