import sbt.*

/** One architecture of the SASS semantics checks, an axis of the project matrix that holds a project for each. */
final case class SassArchitecture(sm: String) extends VirtualAxis.StrongAxis {
  val idSuffix        = sm
  val directorySuffix = s"sm$sm"
}

object SassArchitecture {
  private val listing = "isa/semantics-check/src/main/resources/net/kurobako/gemstone/isa/check/sass-architectures.txt"

  /** The architectures `listing` names, as the numbers after `sm_`. */
  def all(root: File): Seq[SassArchitecture] =
    IO.readLines(root / listing).map(_.trim).filter(_.nonEmpty).map(line => SassArchitecture(line.stripPrefix("sm_")))
}
