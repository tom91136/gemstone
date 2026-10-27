package net.kurobako.gemstone.semantics

object Primitive {
  trait Unary   { def apply(first: Long): Long                            }
  trait Binary  { def apply(first: Long, second: Long): Long              }
  trait Ternary { def apply(first: Long, second: Long, third: Long): Long }
}

/** A named pure function on 64-bit values. Execution applies `function` and rendering calls `name`, the qualified path
  * of the prelude function that wraps the same implementation.
  */
case class Primitive[F](name: String, function: F)
