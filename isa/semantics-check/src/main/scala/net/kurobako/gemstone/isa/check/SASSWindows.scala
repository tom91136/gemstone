package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.flow.Windows
import net.kurobako.gemstone.isa.sass.DecodedInstruction
import net.kurobako.gemstone.vm.RegisterUse

/** The windows of a SASS kernel that an equivalence proof can take: runs of instructions [[SASSForms]] admits on
  * `target`. A guarded write reads the slot it may leave unchanged, so it never ends a live range. The target is one of
  * [[SASSModel.targets]].
  */
object SASSWindows {

  def liveness(graph: ControlFlowGraph, uses: Vec[RegisterUse]): Liveness = Liveness.of(graph, uses)

  /** Whether each decoded instruction has a form a proof can take on `target`. */
  def admitted(target: String, instructions: Vec[DecodedInstruction]): Vec[Boolean] =
    instructions.map(instruction => SASSForms.of(target, instruction.operation).isRight)

  def of(
      graph: ControlFlowGraph,
      uses: Vec[RegisterUse],
      instructions: Vec[DecodedInstruction],
      target: String,
      size: Int
  ): Vec[Window] = {
    val kept = admitted(target, instructions)
    Windows.of(graph, liveness(graph, uses), index => !kept(index), size)
  }

  def of(image: Vec[Byte], function: String, target: String, size: Int = 8): Either[String, Vec[Window]] =
    SASSModel.staticCode(image, function).map((graph, uses, instructions) =>
      of(graph, uses, instructions, target, size)
    )
}
