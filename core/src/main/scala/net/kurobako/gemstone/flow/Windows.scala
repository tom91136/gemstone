package net.kurobako.gemstone.flow

import net.kurobako.gemstone.collection.*

/** The straight-line instructions `first` to `last`, inclusive, of one basic block, with the units live on entry to the
  * first and on exit from the last.
  */
case class Window(block: Int, first: Int, last: Int, liveIn: Set[String], liveOut: Set[String]) derives CanEqual

object Windows {

  /** The windows of at most `size` instructions inside each block of `graph`, with none of the instructions `rejected`
    * holds for. Each run of unrejected instructions in a block gives its windows: the run itself when it is no longer
    * than `size`, otherwise every run of `size` instructions in it.
    */
  def of(
      graph: ControlFlowGraph,
      liveness: Liveness,
      rejected: Int => Boolean,
      size: Int = 8
  ): Vec[Window] =
    graph.blocks.zipWithIndex.flatMap { (block, index) =>
      runs(block.first to block.last, rejected).flatMap { run =>
        val length = math.min(size, run.size)
        run.sliding(length).map { span =>
          Window(index, span.head, span.last, liveness.liveIn(span.head), liveness.liveOut(span.last))
        }
      }
    }

  private def runs(indices: Range, rejected: Int => Boolean): Vec[Vec[Int]] =
    Vec.from(indices.foldLeft(List(Vector.empty[Int])) { (found, index) =>
      if (rejected(index)) Vector.empty[Int] :: found else (found.head :+ index) :: found.tail
    }.reverse.filter(_.nonEmpty).map(Vec.from(_)))
}
