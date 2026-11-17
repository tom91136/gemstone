package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.flow.Windows

/** The windows of an AMDGPU kernel that an equivalence proof can take: runs of instructions of a mnemonic and encoding
  * that has checked cases on `target`, none of which [[Rejection]] refuses. The target is one of
  * [[AMDGPUGenerator.targets]].
  */
object AMDGPUWindows {
  private val Width   = "_(e32|e64)$".r
  private val Variant = "_(sdwa|dpp8?)$".r

  /** The mnemonic of `assembly` or of a case's instance, with its sdwa or dpp encoding and without its width. */
  def encoded(assembly: String): String = Width.replaceFirstIn(assembly.trim.takeWhile(!_.isWhitespace), "")

  def of(image: Vec[Byte], function: String, target: String, size: Int = 8): Either[String, Vec[Window]] =
    AMDGPUModel.staticCode(image, function).map { (graph, uses, assembly) =>
      val encodings = assembly.map(encoded(_))
      val cases     = AMDGPUGenerator.cases(encodings.map(Variant.replaceFirstIn(_, "")).toSet)
        .filter(_.target.contains(target)).groupBy(kase => encoded(kase.instance))
      val admitted = encodings.map(name => cases.get(name).exists(_.forall(Rejection.of(_).isEmpty)))
      Windows.of(graph, Liveness.of(graph, uses), index => !admitted(index), size)
    }
}
