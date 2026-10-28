package net.kurobako.gemstone

/** Orders earlier plain loads before later loads, for validating optimistic reads. */
import java.lang.invoke.VarHandle

private object PlatformFence {
  def acquire(): Unit = VarHandle.acquireFence()
}
