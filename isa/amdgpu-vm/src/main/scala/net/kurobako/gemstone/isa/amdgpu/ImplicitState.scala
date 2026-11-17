package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.string.*

/** The implicit state semantics read and write, spelt as [[InstructionSemantics.describe]] spells it. */
object ImplicitState {
  val names: Vec[String] =
    Vec(RenderContext.Scc, RenderContext.Vcc, RenderContext.Exec, RenderContext.M0, RenderContext.Mode)

  val Exec: String = RenderContext.Exec

  /** The state an operand of `kind` stands for, by the state its kind ends with. */
  def of(kind: OperandKind): Option[String] = names.find(kind.toString.toLowerCaseRoot.endsWith(_))

  /** The 32-bit units of `state` in a wave of `waveSize` lanes: a lane mask is a low and a high half in a wave64, and
    * the low half alone in a wave32.
    */
  def units(state: String, waveSize: Int): Vec[String] = state match {
    case RenderContext.Vcc | RenderContext.Exec =>
      if (waveSize == 64) Vec(s"${state}_lo", s"${state}_hi") else Vec(s"${state}_lo")
    case other => Vec(other)
  }
}
