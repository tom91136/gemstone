package net.kurobako.gemstone.testing

private[testing] object host {
  def variable(name: String): Option[String] = sys.env.get(name).filter(_.nonEmpty)

  def availableThreads: Long = sys.runtime.availableProcessors()
}
