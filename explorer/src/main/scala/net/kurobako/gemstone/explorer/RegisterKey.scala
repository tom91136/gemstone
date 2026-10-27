package net.kurobako.gemstone.explorer

object RegisterKey {

  /** The register `name` within the function that issued `step`, because a caller and a callee may number slots alike.
    */
  def of(step: TraceStep, name: String): RegisterKey =
    RegisterKey(step.location.function.fold("")(_.raw), name)
}

/** A register within the frame of one function. */
private case class RegisterKey(function: String, name: String)
