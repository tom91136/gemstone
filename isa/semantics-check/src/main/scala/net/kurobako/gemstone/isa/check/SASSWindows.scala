package net.kurobako.gemstone.isa.check

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.flow.Windows
import net.kurobako.gemstone.isa.sass.DecodedInstruction
import net.kurobako.gemstone.isa.sass.Operand
import net.kurobako.gemstone.vm.RegisterUse

/** The windows of a SASS kernel that an equivalence proof can take: runs of instructions whose mnemonic has checked
  * cases on `target`, none of which [[Rejection]] refuses. A guarded write reads the slot it may leave unchanged, so it
  * never ends a live range. The target is one of [[SASSModel.targets]].
  */
object SASSWindows {

  def liveness(graph: ControlFlowGraph, uses: Vec[RegisterUse]): Liveness = Liveness.of(graph, uses)

  private val verdicts = ConcurrentHashMap[(String, String), Boolean]()

  /** The mnemonic of a case's instance, without its modifiers or guard. */
  def mnemonic(instance: String): String = {
    val head = instance.split(' ').find(token => !token.startsWith("@")).getOrElse(instance)
    head.takeWhile(_ != '.')
  }

  /** Whether each decoded instruction's mnemonic has checked cases on `target`, all of which [[Rejection]] admits, and
    * whether the instruction reads no constant bank. Cases with a constant operand are left out of the verdict and a
    * decoded instruction that reads one is refused, because the rendered definition reaches such a read as a context
    * call rather than a plain memory access, and the cases carry no other constant for it to stand for.
    */
  def admitted(target: String, instructions: Vec[DecodedInstruction]): Vec[Boolean] = {
    val mnemonics = instructions.map(_.mnemonic.raw).toSet
    val unknown   = mnemonics.filterNot(name => verdicts.containsKey((target, name)))
    if (unknown.nonEmpty) {
      val cases = SASSGenerator.describe(target, unknown)
        .filterNot(kase => kase.instance.contains("c["))
        .groupBy(kase => mnemonic(kase.instance))
      unknown.foreach(name => verdicts.put((target, name), cases.get(name).exists(_.forall(Rejection.of(_).isEmpty))))
    }
    instructions.map(instruction =>
      !readsConstantBank(instruction) && verdicts.get((target, instruction.mnemonic.raw))
    )
  }

  /** Whether any operand of `instruction` reads the constant bank. */
  private def readsConstantBank(instruction: DecodedInstruction): Boolean = {
    def constant(operand: Operand): Boolean = operand match {
      case _: Operand.ConstantBank                           => true
      case Operand.Group(values)                             => values.exists(constant)
      case Operand.Address(base, index, _, _, _, descriptor) =>
        (base.toList ++ index.toList ++ descriptor.toList).exists(constant)
      case Operand.Unary(value = value) => constant(value)
      case Operand.Qualified(value, _)  => constant(value)
      case _                            => false
    }
    instruction.operands.exists(constant)
  }

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
