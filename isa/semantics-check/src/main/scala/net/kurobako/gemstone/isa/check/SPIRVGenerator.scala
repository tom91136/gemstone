package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.explorer.*

/** Every SPIR-V instance the explorer describes, checked in the execution environment whose semantics it renders:
  * kernels for most, and shaders where kernels fault rather than resolve an out-of-range field or conversion, or where
  * only shaders have matrices.
  */
object SPIRVGenerator extends Generator {
  val Kernel: String = "Kernel"
  val Shader: String = "GLCompute"

  def sources: Vec[(name: String, source: String)] = Emit.sources(
    "SPIRV",
    "SPIRVSuite",
    SPIRVModel.all.flatMap(entry =>
      SPIRVInstances.instances(entry.mnemonic).flatMap(instance =>
        instance.described.map(definition =>
          Case(
            instance.text,
            Some(if (instance.signature.shader) Shader else Kernel),
            definition,
            Lookup.Mnemonic(entry.mnemonic)
          )
        )
      )
    )
  )
}
