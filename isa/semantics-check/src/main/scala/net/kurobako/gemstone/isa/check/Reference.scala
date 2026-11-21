package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.Space
import net.kurobako.gemstone.semantics.render.*
import scala.annotation.unused
import scala.util.Random

/** How an ISA executes one instruction, and the machine state its definitions' contexts read and write: a harness `H`
  * whose givens are those contexts. A case carries the lookup `L` of its instance.
  */
trait Reference[H, L <: Lookup] {

  /** A fresh machine for one run, the same for every `seed`. */
  def harness(seed: Long): H

  /** The results of executing `instance` on `target` with the parameters at `inputs`, in order, or why it cannot run.
    */
  def execute(
      target: Option[String],
      instance: String,
      lookup: L,
      parameters: Vec[(String, Type)],
      inputs: Vec[Long],
      results: Vec[(String, Type)],
      harness: H
  ): Either[String, Vec[Long]]

  /** The machine state an instruction may change, such as memory, to compare after both runs. */
  def state(harness: H): Any

  /** The bytes of memory a run loaded, by address. Two sequences are compared on them; a definition is not compared
    * with execution on them, since it describes one lane and execution may load for the others of its wave.
    */
  def loads(@unused harness: H): Set[(Long, Byte)] = Set.empty

  /** Sets the bytes of `space` that `harness` holds before any write, by address, where the ISA can. */
  def seed(@unused harness: H, @unused space: Space, @unused bytes: Map[Long, Byte]): Unit = ()

  /** Whether `state` reflects memory writes, so a memory live-out is comparable. */
  def observesMemory: Boolean = true

  /** The lane of a wave a run executes, where the ISA runs an instruction once per lane. */
  def lane: Option[Int] = None

  /** The inputs of a run of `instance`: the edge-biased `sampled` ones, or others where the instance reads operands
    * that are seldom meaningful at random, such as resource descriptors.
    */
  def inputs(
      @unused target: Option[String],
      @unused instance: String,
      @unused lookup: L,
      @unused parameters: Vec[(String, Type)],
      sampled: Vec[Long],
      @unused random: Random
  ): Vec[Long] = sampled
}
