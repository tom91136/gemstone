package net.kurobako.gemstone

/** Orders earlier plain loads before later loads, for validating optimistic reads. */
private object PlatformFence {
  def acquire(): Unit = java.lang.invoke.VarHandle.acquireFence()
}
