package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.semantics.render.*

/** Every SASS instance the explorer describes on a representative target of each architecture generation, checked
  * against executing it.
  */
object SASSGenerator {

  private val counters = Vec("SR_CLOCK", "SR_GLOBALTIMER")

  /** Each definition `target` describes once, but those that access memory, with the first instance that lowers to it.
    */
  private def describe(target: String): Vec[Case] =
    SASSModel.entries(Some(target)).flatMap(entry =>
      entry.forms.flatMap(form =>
        SASSModel.instances(Some(target), entry.mnemonic, form.name).toOption.toVec.flatten.flatMap(found =>
          // counters are never staged into a checked definition
          SASSModel.definition(found, entry.mnemonic, form.name).toOption.filter(described =>
            !NvidiaCases.excluded(described) && !counters.exists(found.text.contains)
          )
            .map(described =>
              Case(found.text, Some(target), described, Lookup.Sass(found.operands, found.constants, found.written))
            )
        )
      )
    ).distinctBy(value => ScalaBackend.show(value.definition))

  /** The cases of Maxwell, Turing and Ampere, of Hopper, and of both Blackwell families, each compiled on its own as
    * together they outgrow the build's heap.
    */
  val earlier: Generator   = Part("SASS", Vec("sm_50", "sm_75", "sm_86"))
  val hopper: Generator    = Part("SASSHopper", Vec("sm_90"))
  val blackwell: Generator = Part("SASSBlackwell", Vec("sm_100", "sm_120"))

  private class Part(prefix: String, parts: Vec[String]) extends Generator {
    def sources: Vec[(name: String, source: String)] =
      NvidiaCases.sources(prefix, "SASSSuite", parts.flatMap(describe(_)))
  }
}
