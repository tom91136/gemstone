package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

/** The implicit state semantics read and write, spelt as [[InstructionSemantics.describe]] spells it. */
object ImplicitState {
  val names: Vec[String] =
    Vec(RenderContext.Scc, RenderContext.Vcc, RenderContext.Exec, RenderContext.M0, RenderContext.Mode)
}
