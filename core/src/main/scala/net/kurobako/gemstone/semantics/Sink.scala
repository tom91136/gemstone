package net.kurobako.gemstone.semantics

/** Consumers of an instruction's semantics by operand count: a lane loop to execute, operand names to render. */
object Sink {
  trait Binary[V, R] {
    inline def apply(inline kernel: (V, V) => V): R
  }
}
