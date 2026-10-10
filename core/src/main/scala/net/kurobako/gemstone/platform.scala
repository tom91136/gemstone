package net.kurobako.gemstone

/** The narrow cross-platform boundary for wall-clock and monotonic time. */
object platform {
  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def epochMillis: Long = System.currentTimeMillis()

  @SuppressWarnings(Array("scalafix:ForbiddenAPIs"))
  def monotonicNanos: Long = System.nanoTime()
}
