package net.kurobako.gemstone

import scala.scalanative.libc.stdatomic

/** Orders earlier plain loads before later loads, for validating optimistic reads. */
private object PlatformFence {
  def acquire(): Unit = stdatomic.atomic_thread_fence(stdatomic.memory_order.memory_order_acquire)
}
