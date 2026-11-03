package net.kurobako.gemstone.profile

/** The packed records a [[ProfileRecorder]] writes and a [[ProfileStream]] replays. Each starts with a header whose low
  * four bits are its tag.
  */
private object ProfileRecords {
  inline val IssueTag: 1L      = 1L
  inline val AccessTag: 2L     = 2L
  inline val AsyncTag: 3L      = 3L
  inline val TransitionTag: 4L = 4L
  val BeginsBit: Long          = 1L << 28

  /** How many longs the record that `header` starts takes. */
  def recordLength(header: Long): Int = (header & 0xfL) match {
    case IssueTag  => 5 + ((header >>> 4) & 0xffL).toInt
    case AccessTag => 2
    case AsyncTag  => 3
    case _         => 1
  }
}
