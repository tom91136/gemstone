package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.flow.Windows
import net.kurobako.gemstone.isa.amdgpu.ImplicitState
import net.kurobako.gemstone.isa.amdgpu.Target
import net.kurobako.gemstone.vm.RegisterUse

/** The windows of an AMDGPU kernel that an equivalence proof can take: runs of instructions [[AMDGPUForms]] admits on
  * `target`, none of which writes EXEC, so both sides of a proof run their lanes under one mask. A vector register
  * keeps the value of every lane a write leaves inactive, so no write ends its live range. The target is one of
  * [[AMDGPUGenerator.targets]] or shares the ISA of one.
  */
object AMDGPUWindows {
  private val VectorUnit = "[va][0-9]+".r

  def liveness(graph: ControlFlowGraph, uses: Vec[RegisterUse]): Liveness =
    Liveness.of(graph, uses, VectorUnit.matches(_))

  def of(image: Vec[Byte], function: String, target: String, size: Int = 8): Either[String, Vec[Window]] = for {
    named <- target.refine[Target].left.map(_.raw)
    found <- AMDGPUModel.staticCode(image, function)
  } yield {
    val (graph, uses, assembly) = found
    val kept                    = assembly.map(AMDGPUForms.of(named, _).isRight)
    val exec                    = ImplicitState.units(ImplicitState.Exec, 64).toSet
    Windows.of(graph, liveness(graph, uses), index => !kept(index) || uses(index).writes.exists(exec), size)
  }
}
