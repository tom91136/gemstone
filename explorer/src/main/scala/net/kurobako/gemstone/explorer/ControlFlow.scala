package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.flow.*

object ControlFlow {
  case class Block(first: Long, last: Long, successors: Vec[Int], open: Boolean)

  def of(graph: ControlFlowGraph, address: Int => Long): ControlFlow =
    ControlFlow(
      graph.blocks.map(block => Block(address(block.first), address(block.last), block.successors, block.open)),
      graph.loops
    )
}

case class ControlFlow(blocks: Vec[ControlFlow.Block], loops: Vec[Loop])
