package net.kurobako.gemstone.explorer

import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.collection.*

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
  final def semantics(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] =
    found.computeIfAbsent((entry, form), _ => render(entry, form))

  protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]]

  private val found = ConcurrentHashMap[(View.Entry, View.Form), Either[Unavailable, Vec[Described]]]()
}
