package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.semantics.render.*

/** What the NVIDIA checks leave out: definitions that access memory, whose addresses come from inputs drawn at random
  * and so fault for most of them.
  */
object NvidiaCases {
  def included(definition: Definition): Boolean =
    definition.contexts.forall(context => context.tpe.split('.').last != "Memory")

  /** Whether `definition` is one element of a matrix product, from the rows and column it reads to `d`. */
  def product(definition: Definition): Boolean =
    definition.parameters.exists(_.name == "a0") && definition.results.map(_.name) == Vec("d")

  /** [[Emit.sources]] in groups small enough that a suite checking matrix products, whose many operands each take a
    * typed parameter, stays within the JVM's method size.
    */
  def sources(prefix: String, suite: String, cases: Vec[Case]): Vec[(name: String, source: String)] =
    cases.grouped(50).zipWithIndex.flatMap((group, index) => Emit.sources(s"$prefix${index}x", suite, group)).toVec
}
