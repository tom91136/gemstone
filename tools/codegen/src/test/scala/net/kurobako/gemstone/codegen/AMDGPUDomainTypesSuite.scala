package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.codegen.OperandDomain.*

class AMDGPUDomainTypesSuite extends munit.FunSuite {
  private val registers = Registers("sgpr", first = 0, last = 105, code = 0)

  test("a domain spans the widest bounds and every name its ISA families list") {
    val shape = AMDGPUDomainTypes.shapeOf(Vector(
      Union(Vector(registers, Named("vcc_lo", 106), IntegerRange(first = 0, last = 64, code = 128))),
      Union(Vector(
        Registers("sgpr", first = 0, last = 101, code = 0),
        Named("tba_lo", 108),
        IntegerRange(first = -1, last = -16, code = 193),
        Literal(bytes = 4, code = 255)
      ))
    )).get
    assertEquals(shape.registers.toMap, Map("sgpr" -> 105))
    assertEquals(shape.named, Vector("vcc_lo", "tba_lo"))
    assertEquals(shape.integers, Some((-16, 64)))
    assertEquals(shape.literals, Set(4))
  }

  test("partitioned and untyped domains have no type") {
    assertEquals(AMDGPUDomainTypes.shapeOf(Vector(Partitioned(Vector.empty))), None)
    assertEquals(AMDGPUDomainTypes.shapeOf(Vector(Ignored)), None)
    assertEquals(AMDGPUDomainTypes.shapeOf(Vector(Unmodelled)), None)
  }

  test("the rendered enum carries refined register cases and converts to slots") {
    val shape =
      AMDGPUDomainTypes.shapeOf(Vector(Union(Vector(registers, Named("m0", 125), Literal(bytes = 4, code = 255))))).get
    val source = AMDGPUDomainTypes.render("ssrc", shape)
    assert(source.contains("enum Ssrc extends DomainValue"), source)
    assert(source.contains("case Sgpr(index: Int in 0 ~ 105)"), source)
    assert(source.contains("case M0"), source)
    assert(source.contains("case Literal"), source)
    assert(source.contains("""case Slot.Named("m0") => Some(Ssrc.M0)"""), source)
  }

  test("a case named like its domain is called Value") {
    val shape  = AMDGPUDomainTypes.shapeOf(Vector(Registers("vgpr", first = 0, last = 255, code = 0))).get
    val source = AMDGPUDomainTypes.render("vgpr", shape)
    assert(source.contains("case Value(index: Int in 0 ~ 255)"), source)
    assertEquals(AMDGPUDomainTypes.typeName("simm8@7"), "Simm8At7")
  }

  test("sources lists one file per typed domain and an index") {
    val (files, untyped) = AMDGPUDomainTypes.sources(
      Vector("ssrc" -> registers, "ssrc" -> Named("m0", 125), "hwreg" -> Partitioned(Vector.empty)),
      Path.of("domain")
    )
    assertEquals(files.map(_._1.getFileName.toString), Vector("Ssrc.scala", "OperandDomains.scala"))
    assertEquals(untyped, Vector("hwreg"))
  }

  test("immediate rules replace the immediate of a type or add one") {
    val rule    = AMDGPUDomainRules.ImmediateRule(Set("GFX9"), "SMEM_OFFSET", Immediate(21, signed = true), "r", "s")
    val entries = Vector(AMDGPUOperandDomains.Entry(
      "SMEM_OFFSET",
      Union(Vector(registers, Immediate(21, signed = false))),
      Vector.empty,
      "CDNA1"
    ))
    val edited = AMDGPUOperandDomains.immediates("GFX9", entries, Vector(rule)).head.domain
    assertEquals(edited, Union(Vector(registers, Immediate(21, signed = true))))
    assertEquals(AMDGPUOperandDomains.immediates("GFX8", entries, Vector(rule)), entries)
    val bare = AMDGPUOperandDomains.Entry("SMEM_OFFSET", registers, Vector.empty, "CDNA1")
    assertEquals(
      AMDGPUOperandDomains.immediates("GFX9", Vector(bare), Vector(rule)).head.domain,
      Union(Vector(registers, Immediate(21, signed = true)))
    )
  }
}
