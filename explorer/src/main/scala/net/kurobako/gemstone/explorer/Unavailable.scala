package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

object Unavailable {

  /** The reason the failures of a form's instances give: instances that lower to operations the VM runs as mechanics
    * decide it before those the VM rejects or that are malformed, and those before instances whose steps form no
    * definition.
    */
  def of(failures: Vec[Unrendered]): Unavailable = {
    val staged   = failures.collect { case Unrendered.Unstaged(operations) => operations }
    val unstaged = staged.flatten.distinct
    failures.collectFirst { case Unrendered.Rejected(reason) if unstaged.isEmpty => NotImplemented(reason) }
      .orElse(failures.collectFirst {
        case Unrendered.Malformed(reason) if unstaged.isEmpty => NotInstantiated(reason)
      })
      .orElse(Option.when(staged.nonEmpty)(Mechanics(s"Lowers to ${unstaged.mkString(", ")}.")))
      .orElse(failures.collectFirst { case Unrendered.Undefined(reason) => NotInstantiated(reason) })
      .getOrElse(Mechanics(failures.headOption.fold("")(_.message)))
  }
}

/** Why no instance of a form has a definition, as the pipeline that renders one finds it. */
enum Unavailable(val summary: String, val detail: Option[String] = None) derives CanEqual {

  /** The VM rejects the form, so a kernel that uses it does not load. */
  case NotImplemented(message: String) extends Unavailable("Not implemented in the VM.", Some(message))

  /** The VM runs the form as machine mechanics, such as control flow or a barrier, not as a per-lane definition. */
  case Mechanics(message: String)
      extends Unavailable("Runs in the VM as machine mechanics, with no per-lane definition.", Some(message))

  /** Outside what the VM models, such as graphics, scheduling hints or vendor extensions. */
  case NotModelled(message: String) extends Unavailable("Not modelled: outside compute execution.", Some(message))

  /** No instance of the form can be written, so nothing reaches the VM. */
  case NotInstantiated(message: String)
      extends Unavailable("No instance of this form can be written yet.", Some(message))

  /** Not yet classified. */
  case Unclassified extends Unavailable("Not yet described.")
}
