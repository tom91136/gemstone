package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.semantics.render.*

/** Every PTX instance the explorer describes, checked against executing it. */
object PTXGenerator extends Generator {

  /** Each instance once, with the PTX type each register it names is declared as. */
  lazy val instances: Vec[(mnemonic: String, text: String, registers: Vec[(name: String, declared: String)])] =
    PTXModel.entries(None).flatMap(entry =>
      entry.forms.flatMap(form =>
        PTXModel.instances(entry.mnemonic, form.name).map((instance, declared) =>
          (mnemonic = entry.mnemonic, text = instance, registers = declared)
        )
      )
    ).distinctBy(_.text)

  /** Each instance once, and each distinct matrix product definition once with the first instance that renders it:
    * products share their arithmetic across layouts, shapes and spellings.
    */
  def sources: Vec[(name: String, source: String)] =
    NvidiaCases.sources(
      "PTX",
      "PTXSuite",
      instances.flatMap((mnemonic, instance, declared) =>
        InstructionSemantics.describe(mnemonic, instance, declared).toOption
          .filter(definition => !NvidiaCases.excluded(definition))
          .map(Case(instance, None, _, Lookup.Registers(declared)))
      ).distinctBy(found =>
        if (NvidiaCases.product(found.definition)) Right(ScalaBackend.show(found.definition)) else Left(found.instance)
      )
    )
}
