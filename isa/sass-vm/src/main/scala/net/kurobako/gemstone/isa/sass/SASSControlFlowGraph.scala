package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.*

object SASSControlFlowGraph {
  def of(sm: Int >= 35, function: DecodedFunction): ControlFlowGraph = {
    val instructions                      = function.instructions
    val successors: Vec[Option[Vec[Int]]] = Generation.of(sm) match {
      case Generation.Modern                      => modern(instructions)
      case Generation.Kepler | Generation.Maxwell =>
        val legacy = LegacyFlowGraph(instructions)
        instructions.indices.toVec.map(legacy.successors(_))
    }
    ControlFlowGraph(successors, 0)
  }

  private def modern(instructions: Vec[DecodedInstruction]): Vec[Option[Vec[Int]]] = {
    val indices = instructions.zipWithIndex.map { case (instruction, index) => instruction.address.raw -> index }.toMap
    def successors(index: Int): Option[Vec[Int]] = {
      val instruction         = instructions(index)
      val next                = Vec(index + 1)
      val resumes             = if (LegacyControlFlow.terminates(instruction)) Vec.empty[Int] else next
      def target: Option[Int] = instruction.operands.lastOption.flatMap(OperandQueries.integer)
        .filter(_.isValidInt).flatMap(value => indices.get(value.toInt))
      if (LegacyControlFlow.never(instruction)) Some(next)
      else instruction.operation.form match {
        case _: Form.Bra | _: Form.Jmp                         => target.map(t => Vec(t) ++ resumes)
        case _: Form.Exit | _: Form.Kill                       => Some(resumes)
        case _: Form.Bssy | _: Form.Bsync | _: Form.Yield      => Some(next)
        case _ if ControlFlow.transfers(instruction.operation) => None
        case _                                                 => Some(next)
      }
    }
    instructions.indices.toVec.map(successors(_))
  }
}
