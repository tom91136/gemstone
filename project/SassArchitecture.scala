import sbt.*

/** One shard of one architecture of the SASS semantics checks, an axis of the project matrix that holds a project for
  * each: an architecture whose cases outgrow the heap is split into shards by the hash of the mnemonic.
  */
final case class SassArchitecture(sm: String, shard: Int, shards: Int) extends VirtualAxis.StrongAxis {
  val id              = if (shards == 1) sm else s"${sm}s$shard"
  val idSuffix        = id
  val directorySuffix = s"sm$id"
}

object SassArchitecture {
  private val listing = "isa/semantics-check/src/main/resources/net/kurobako/gemstone/isa/check/sass-architectures.txt"

  /** The shards of the architectures `listing` names, one per line as `sm_<number>` and an optional count of shards. */
  def all(root: File): Seq[SassArchitecture] =
    IO.readLines(root / listing).map(_.trim).filter(_.nonEmpty).flatMap { line =>
      val fields = line.split("\\s+")
      val shards = fields.drop(1).headOption.fold(1)(_.toInt)
      (0 until shards).map(SassArchitecture(fields(0).stripPrefix("sm_"), _, shards))
    }
}
