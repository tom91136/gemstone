package net.kurobako.gemstone.codegen

import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*

class AMDGPUProcessorsSuite extends Suite {
  private val llvmProcessors = Path.of("target/references/amdgpu/llvm/llvm/lib/Target/AMDGPU/GCNProcessors.td")
  private val definition     = """ProcessorModel<"([^"]+)"""".r

  private val excluded = Map(
    "polaris10" -> "gfx803 marketing name with the same features",
    "polaris11" -> "gfx803 marketing name with the same features",
    "tongapro"  -> "gfx805 marketing name with the same features",
    "gfx1310"   -> "no gfx13 family in the catalogue or domain rules"
  )

  private val mapped = AMDGPUDomainRules.Processors.values.flatten.toSet

  private lazy val llvm = definition.findAllMatchIn(Files.readString(llvmProcessors)).map(_.group(1)).toSet

  test("LLVM processors are in the domain map unless generic or excluded") {
    assume(Files.isRegularFile(llvmProcessors), "missing LLVM GCNProcessors.td; run sbt codegen")
    val expected = llvm.filterNot(name => name.contains("generic") || excluded.contains(name))
    assertEquals((expected -- mapped).toVector.sorted, Vector.empty[String])
  }

  test("named exclusions are LLVM processors without a domain entry") {
    assume(Files.isRegularFile(llvmProcessors), "missing LLVM GCNProcessors.td; run sbt codegen")
    assertEquals(excluded.keys.filter(name => !llvm(name) || mapped(name)).toVector.sorted, Vector.empty[String])
  }

  test("catalogue GFX processors are in the domain map") {
    val catalogue = Architecture.all.map(_.name).filter(_.startsWith("gfx"))
    assertEquals(catalogue.filterNot(mapped), Vec.empty[String])
  }
}
