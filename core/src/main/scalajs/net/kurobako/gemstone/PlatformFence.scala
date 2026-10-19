package net.kurobako.gemstone

/** JavaScript runs one thread per realm, so program order already orders loads. */
private object PlatformFence {
  def acquire(): Unit = ()
}
