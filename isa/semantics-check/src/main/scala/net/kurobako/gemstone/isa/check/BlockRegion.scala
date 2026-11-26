package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Loop
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.BlockAnalysis
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.ProfileStream
import scala.annotation.tailrec

/** The loop or block of a kernel that holds an instruction, and what [[BlockAnalysis]] says of it. */
private[check] object BlockRegion {

  /** `order` is the instructions of the region in layout order, and `paths` whether a loop has more than one path
    * through it.
    */
  case class Region(label: String, order: Vec[Int], paths: Boolean)

  def each[A, B](values: Vec[A])(f: A => Either[String, B]): Either[String, Vec[B]] = {
    val found = values.map(f(_))
    found.collectFirst { case Left(reason) => reason }.toLeft(found.collect { case Right(value) => value })
  }

  def innermost(graph: ControlFlowGraph, block: Int): Option[Loop] =
    Option.when(graph.reducible)(graph.loops.filter(_.body.contains(block)).minByOption(_.body.size)).flatten

  /** The region of `function` holding `anchor`, with `offsets` the byte offset of each instruction. */
  def of(function: String, graph: ControlFlowGraph, offsets: Vec[Int], anchor: Int): Region = {
    val block  = graph.blocks.indexWhere(block => block.first <= anchor && anchor <= block.last)
    val loop   = innermost(graph, block)
    val blocks = loop.fold(Vec(block))(_.body.sorted)
    val header = loop.fold(block)(_.header)
    val kind   = loop.fold(BlockAnalysis.Kind.Block)(_ => BlockAnalysis.Kind.Loop)
    val order  = blocks.flatMap(index => Vec.from(graph.blocks(index).first to graph.blocks(index).last))
    val inside = blocks.toSet
    val paths  = loop.nonEmpty && blocks.exists(index => graph.blocks(index).successors.count(inside) != 1)
    val label  = s"$function/${kind.name}@${BlockAnalysis.hex(offsets(graph.blocks(header).first).toLong)}"
    Region(label, order, paths)
  }

  def timing(
      stream: ProfileStream,
      architecture: Architecture,
      placement: Option[PipelineSimulator.Placement],
      label: String
  ): Either[String, BlockAnalysis.Timing] =
    BlockAnalysis.region(stream, architecture, placement, label).flatMap(_.status match {
      case BlockAnalysis.Status.Executed(timing) => Right(timing)
      case BlockAnalysis.Status.Withheld(reason) => Left(s"$label is withheld: $reason")
      case BlockAnalysis.Status.NotExecuted      => Left(s"$label is not executed")
    })

  /** The longest run of equal instructions `left` and `right` share in order, as their positions in each. */
  def common(left: Vec[String], right: Vec[String]): Vec[(Int, Int)] = {
    val length = Array.ofDim[Int](left.size + 1, right.size + 1)
    for {
      i <- left.indices.reverse
      j <- right.indices.reverse
    } length(i)(j) =
      if (left(i) == right(j)) length(i + 1)(j + 1) + 1 else math.max(length(i + 1)(j), length(i)(j + 1))
    @tailrec
    def walk(i: Int, j: Int, found: List[(Int, Int)]): List[(Int, Int)] =
      if (i == left.size || j == right.size) found
      else if (left(i) == right(j)) walk(i + 1, j + 1, (i, j) :: found)
      else if (length(i + 1)(j) >= length(i)(j + 1)) walk(i + 1, j, found)
      else walk(i, j + 1, found)
    Vec.from(walk(0, 0, Nil).reverse)
  }

  /** Whether `architecture` has a probed model of its own. */
  def calibrated(architecture: Architecture): Boolean =
    (architecture.branchRedirect.map(_.source) ++ architecture.issueOrder.map(_.source) ++
      architecture.pipeLatency.values.map(_.source)).exists {
      case Architecture.Source.Probed(_, _, _, _) => true
      case _                                      => false
    }
}
