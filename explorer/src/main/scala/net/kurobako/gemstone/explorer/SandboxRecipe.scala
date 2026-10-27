package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** A small program that demonstrates one machine definition or a control snippet in the instruction sandbox.
  *
  * A recipe is deliberately explicit: it names the instructions, the template target, and the arguments the sample
  * needs. It does not execute anything while a page renders; [[SandboxRunner]] runs it only when the reader asks. ISA
  * workstreams register recipes as their models start returning machine definitions.
  */
case class SandboxRecipe(
    instructions: String,
    summary: String,
    target: Option[String] = None,
    parameters: Vec[SandboxProgram.Parameter] = Vec.empty,
    allocations: Vec[Vec[Byte]] = Vec.empty,
    scalars: Vec[Vec[Byte]] = Vec.empty,
    group: Dimensions = Dimensions.One,
    count: Dimensions = Dimensions.One
) {

  /** The program for `language`, preferring the target the snippet was written for over the selector's target. */
  def program(language: Language, fallbackTarget: Option[String]): SandboxProgram =
    SandboxProgram(
      language = language,
      instructions = instructions,
      target = target.orElse(fallbackTarget),
      parameters = parameters,
      allocations = allocations,
      scalars = scalars,
      group = group,
      count = count
    )
}
