package net.kurobako.gemstone.isa.check

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.flow.Windows
import net.kurobako.gemstone.isa.amdgpu.ImplicitState
import net.kurobako.gemstone.vm.RegisterUse

/** The windows of an AMDGPU kernel that an equivalence proof can take: runs of instructions of a mnemonic and encoding
  * that has checked cases on `target`, none of which [[Rejection]] refuses, and none of which writes EXEC, so both
  * sides of a proof run their lanes under one mask. A vector register keeps the value of every lane a write leaves
  * inactive, so no write ends its live range. The target is one of [[AMDGPUGenerator.targets]].
  */
object AMDGPUWindows {
  private val VectorUnit = "[va][0-9]+".r
  private val Width      = "_(e32|e64)$".r
  private val Variant    = "_(sdwa|dpp8?)$".r

  /** The mnemonic of `assembly` or of a case's instance, with its sdwa or dpp encoding and without its width. */
  def encoded(assembly: String): String = Width.replaceFirstIn(assembly.trim.takeWhile(!_.isWhitespace), "")

  def liveness(graph: ControlFlowGraph, uses: Vec[RegisterUse]): Liveness =
    Liveness.of(graph, uses, VectorUnit.matches(_))

  private val verdicts = ConcurrentHashMap[(String, String), Boolean]()

  /** Whether each encoded mnemonic of `names` has checked cases on `target`, all of which [[Rejection]] admits. */
  def admitted(target: String, names: Set[String]): Map[String, Boolean] = {
    val unknown = names.filterNot(name => verdicts.containsKey((target, name)))
    if (unknown.nonEmpty) {
      val checked = AMDGPUGenerator.checked(target)
      val cases   = AMDGPUGenerator.cases(unknown.map(Variant.replaceFirstIn(_, "")))
        .filter(kase => checked.exists(kase.target.contains)).groupBy(kase => encoded(kase.instance))
      unknown.foreach(name => verdicts.put((target, name), cases.get(name).exists(_.forall(Rejection.of(_).isEmpty))))
    }
    names.map(name => name -> verdicts.get((target, name))).toMap
  }

  def of(image: Vec[Byte], function: String, target: String, size: Int = 8): Either[String, Vec[Window]] =
    AMDGPUModel.staticCode(image, function).map { (graph, uses, assembly) =>
      val encodings = assembly.map(encoded(_))
      val kept      = admitted(target, encodings.toSet)
      val exec      = ImplicitState.units(ImplicitState.Exec, 64).toSet
      Windows.of(
        graph,
        liveness(graph, uses),
        index => !kept(encodings(index)) || uses(index).writes.exists(exec),
        size
      )
    }
}
