package net.kurobako.gemstone.testing

import net.kurobako.gemstone.semantics.*

object Applied {
  @FunctionalInterface
  trait Wrap[R] {
    def apply(value: Long): R
  }
}

/** A sink that runs an atomic's kernel once on a fixed old value and operand, and shapes the result with `wrap`. */
class Applied[R](old: Long, operand: Long)(wrap: Applied.Wrap[R]) extends Sink.Binary[Long, R] {
  inline def apply(inline kernel: (Long, Long) => Long): R = wrap(kernel(old, operand))
}
