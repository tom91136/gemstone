package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.RegisterUse
import scala.annotation.tailrec

object Liveness {
  private val Span   = raw"(.*)\[(\d+):(\d+)\]".r
  private val Number = raw"(.*?)(\d+)".r

  /** The 32-bit units `name` spans: each index of a range such as `s[2:3]`, the `width` units from a register such as
    * `R4`, or the name itself for a predicate or a register without an index.
    */
  def units(name: String, width: Int): Vec[String] = name match {
    case Span(prefix, first, last)          => Vec.from((first.toInt to last.toInt).map(prefix + _))
    case Number(prefix, first) if width > 1 => Vec.from((first.toInt until first.toInt + width).map(prefix + _))
    case _                                  => Vec(name)
  }

  private def unitsOf(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => units(name, use.unitsOf(name))).toSet

  /** Liveness of the units `uses` names, before and after each instruction, over `graph`. A write ends the live range
    * of the units it spans, and a block with unresolved successors keeps every unit the code names live out of it.
    */
  def of(graph: ControlFlowGraph, uses: Vec[RegisterUse]): Liveness = {
    val reads  = uses.map(use => unitsOf(use, use.reads))
    val writes = uses.map(use => unitsOf(use, use.writes))
    val named  = (reads ++ writes).flatten.toSet
    val blocks = graph.blocks

    def through(block: Block, out: Set[String]): Set[String] = (block.last to block.first by -1).foldLeft(out)(
      (live, index) => (live -- writes(index)) ++ reads(index)
    )

    @tailrec
    def solve(liveIn: Vec[Set[String]]): Vec[Set[String]] = {
      val next = blocks.map { block =>
        val out = if (block.open) named else block.successors.flatMap(liveIn(_)).toSet
        through(block, out)
      }
      if (next == liveIn) liveIn else solve(next)
    }

    val blockIn  = solve(blocks.map(_ => Set.empty[String]))
    val blockOut = blocks.map(block => if (block.open) named else block.successors.flatMap(blockIn(_)).toSet)
    val after    = Array.fill(uses.size)(Set.empty[String])
    val before   = Array.fill(uses.size)(Set.empty[String])
    blocks.indices.foreach { b =>
      val block = blocks(b)
      (block.last to block.first by -1).foldLeft(blockOut(b)) { (live, index) =>
        after(index) = live
        val entering = (live -- writes(index)) ++ reads(index)
        before(index) = entering
        entering
      }
    }
    Liveness(Vec.from(before), Vec.from(after))
  }
}

/** The units live on entry to and on exit from each instruction. */
case class Liveness(liveIn: Vec[Set[String]], liveOut: Vec[Set[String]]) derives CanEqual
