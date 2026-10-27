package net.kurobako.gemstone.explorer

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** One ISA's catalogue, register files and translators, narrowed by target where the ISA has targets. */
trait Model {

  /** Selectable targets; `None` selects every target and is only offered when `anyTarget` holds. */
  def targets: Vec[String]
  def anyTarget: Boolean
  def defaultTarget: Option[String]

  def entries(target: Option[String]): Vec[View.Entry]
  def registers(target: Option[String]): Vec[View.RegisterFile]

  /** Whether the ISA has a binary form; text ISAs only parse, validate and print. */
  def binary: Boolean
  def assemble(source: String, target: Option[String]): Either[String, String]
  def disassemble(bytes: Vec[Byte], target: Option[String]): Either[String, String]
  def assemblyExample: String
  def binaryExample: String

  /** The semantics of instances of `form`, each under the operand table's names, or why no instance has any; found once
    * per form.
    */
  final def semantics(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] = {
    retarget(entry.target)
    found.computeIfAbsent((entry, form), _ => render(entry, form))
  }

  /** The semantics of instances of `form` as the explorer shows them: the lane definitions of [[semantics]] and the
    * machine definitions of [[machineDefinitions]]. ISA models with machine semantics override this; the default has
    * none.
    */
  def semanticsOf(entry: View.Entry, form: View.Form): Semantics = Semantics.of(semantics(entry, form), Vec.empty)

  /** The machine definitions of instances of `form`, found by `find` once per form. */
  protected final def machineDefinitions(
      entry: View.Entry,
      form: View.Form
  )(find: => Vec[Described]): Vec[Described] = {
    retarget(entry.target)
    machined.computeIfAbsent((entry, form), _ => find)
  }

  protected final def withMachineDefinitions(
      entry: View.Entry,
      form: View.Form,
      defined: Vec[Described],
      reasons: Vec[String]
  ): Semantics = semantics(entry, form) match {
    case Left(Unavailable.Mechanics(message)) if defined.isEmpty && reasons.nonEmpty =>
      Semantics.Unavailable(
        Unavailable.Mechanics(s"$message Its machine definition fails: ${reasons.mkString("; ")}")
      )
    case lane => Semantics.of(lane, defined)
  }

  protected final def withMachineOutcomes(
      entry: View.Entry,
      form: View.Form,
      outcomes: Vec[(instance: String, outcome: Either[Unrendered, Definition])]
  ): Semantics = {
    val defined = machineDefinitions(entry, form)(
      outcomes.flatMap((instance, outcome) => outcome.toOption.map(Described(instance, _)))
    )
    val reasons = outcomes.collect { case (_, Left(Unrendered.Undefined(reason))) => reason }.distinct
    withMachineDefinitions(entry, form, defined, reasons)
  }

  protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]]

  /** Drops the machine outcomes an ISA model caches in its own map. */
  protected def clearOutcomes(): Unit = ()

  // the caches hold one target's definitions, so a walk over the targets stays bounded
  private var cachedTarget: Option[String] = None

  private def retarget(target: Option[String]): Unit =
    if (target != cachedTarget) {
      cachedTarget = target
      found.clear()
      machined.clear()
      clearOutcomes()
    }

  private val found    = ConcurrentHashMap[(View.Entry, View.Form), Either[Unavailable, Vec[Described]]]()
  private val machined = ConcurrentHashMap[(View.Entry, View.Form), Vec[Described]]()
}
