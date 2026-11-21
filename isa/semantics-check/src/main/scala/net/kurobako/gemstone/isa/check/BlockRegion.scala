package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Loop
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.BlockAnalysis
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.ProfileStream

/** The loop or block of a kernel that holds an instruction, and what [[BlockAnalysis]] says of it. */
private[check] object BlockRegion {

  /** `order` is the instructions of the region in layout order, and `paths` whether a loop has more than one path
    * through it.
    */
  case class Region(label: String, kind: BlockAnalysis.Kind, order: Vec[Int], paths: Boolean)

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
    Region(label, kind, order, paths)
  }

  def timing(
      stream: ProfileStream,
      architecture: Architecture,
      placement: Option[PipelineSimulator.Placement],
      label: String
  ): Either[String, BlockAnalysis.Timing] = for {
    analysis <- BlockAnalysis.of(stream, architecture, placement)
    regions  <- analysis.select(Some(label))
    timing   <- regions.head.status match {
      case BlockAnalysis.Status.Executed(timing) => Right(timing)
      case BlockAnalysis.Status.Withheld(reason) => Left(s"$label is withheld: $reason")
      case BlockAnalysis.Status.NotExecuted      => Left(s"$label is not executed")
    }
  } yield timing

  /** Whether `architecture` has a probed model of its own. */
  def calibrated(architecture: Architecture): Boolean =
    (architecture.branchRedirect.map(_.source) ++ architecture.issueOrder.map(_.source) ++
      architecture.pipeLatency.values.map(_.source)).exists {
      case Architecture.Source.Probed(_, _, _, _) => true
      case _                                      => false
    }
}
