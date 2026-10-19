package net.kurobako.gemstone.isa.nvidia

/** The value an atomic stores over the previous one, applied when the write buffer commits. */
private trait AtomicUpdate {
  def apply(previous: Long): Long
}
