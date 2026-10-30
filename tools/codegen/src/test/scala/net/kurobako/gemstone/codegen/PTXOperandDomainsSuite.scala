package net.kurobako.gemstone.codegen

import net.kurobako.gemstone.testing.Suite
import org.jsoup.Jsoup

class PTXOperandDomainsSuite extends Suite {
  private def table(caption: String, rows: String): String =
    s"""<table><caption><span class="caption-text">$caption</span></caption><tbody>$rows</tbody></table>"""

  private val fundamental = table(
    "Fundamental Type Specifiers",
    """<tr><th>Basic Type</th><th>Fundamental Type Specifiers</th></tr>
      |<tr><td>Signed integer</td><td>.s8, .s32</td></tr>
      |<tr><td>Floating-point</td><td>.f16, .f16x2, .f32</td></tr>
      |<tr><td>Bits (untyped)</td><td>.b32</td></tr>
      |<tr><td>Predicate</td><td>.pred</td></tr>""".stripMargin
  )

  private val packed = table(
    "Operand types for packed floating point instruction type.",
    """<tr><th>Packed</th><th>Elements</th><th>Element</th><th>Register</th></tr>
      |<tr><td>.f16x2</td><td rowspan="2">Two</td><td>.f16</td><td>.f16x2 or .b32</td></tr>
      |<tr><td>.e4m3x2</td><td>.e4m3</td><td>.b16</td></tr>""".stripMargin
  )

  private val agreement = table(
    "Type Checking Rules",
    """<tr><th colspan="2"></th><th colspan="2">Operand Type</th></tr>
      |<tr><td colspan="2"></td><td>.sX</td><td>.fX</td></tr>
      |<tr><td rowspan="2">Instruction Type</td><td>.sX</td><td>okay</td><td>invalid</td></tr>
      |<tr><td>.fX</td><td>invalid</td><td>okay</td></tr>""".stripMargin
  )

  private def relaxed(caption: String, cell: String) = table(
    caption,
    s"""<tr><th colspan="2" rowspan="2"></th><th colspan="2">Operand Type</th></tr>
       |<tr><th>s32</th><th>f32</th></tr>
       |<tr><td rowspan="2">Instruction Type</td><td>s32</td><td>-</td><td>$cell</td></tr>
       |<tr><td>f32</td><td>inv</td><td>-</td></tr>
       |<tr><td colspan="4">Notes</td></tr>""".stripMargin
  )

  private def read(source: String = relaxed("Relaxed Type-checking Rules for Source Operands", "inv")) =
    PTXReferenceTables.read(
      Jsoup.parse(
        s"<html><body>$fundamental$packed$agreement$source" +
          relaxed("Relaxed Type-checking Rules for Destination Operands", "inv") + "</body></html>"
      )
    )

  test("spanned cells are repeated into every position they cover") {
    val tables = read()
    assertEquals(tables.packed.map(_.elements), Vector(2, 2))
    assertEquals(tables.packed.map(_.declared), Vector(Vector("f16x2", "b32"), Vector("b16")))
    assertEquals(tables.agreement("sX", "fX"), Some("invalid"))
    assertEquals(tables.agreement("fX", "fX"), Some("okay"))
    assertEquals(tables.source.columns, Vector("s32", "f32"))
    assertEquals(tables.source("s32", "f32"), Some("inv"))
    assertEquals(tables.source.rows.map(_.label), Vector("s32", "f32"))
  }

  test("types come from the fundamental, packed and cited tables") {
    val derived             = PTXOperandDomains.derive(Vector.empty, read(), PTXOperandRules.All, Vector.empty)
    def named(name: String) = derived.types.find(_.name == name)
    assertEquals(named("s8").map(_.bits), Some(8))
    assertEquals(named("pred").map(_.bits), Some(1))
    assertEquals(named("f16x2").map(row => (row.kind, row.elements, row.element)), Some(("packed", 2, Some("f16"))))
    assertEquals(named("e4m3x2").map(_.declared), Some(Vector("b16")))
    assertEquals(named("bf16").map(_.declared), Some(Vector("b16")))
    assertEquals(named("e4m3").map(_.kind), Some("alt"))
    assert(derived.findings.exists(finding => finding.check == "type-listed-twice" && finding.subject == ".f16x2"))
  }

  test("a relaxed table that contradicts the type checking table is a finding") {
    val derived = PTXOperandDomains.derive(
      Vector.empty,
      read(relaxed("Relaxed Type-checking Rules for Source Operands", "-")),
      PTXOperandRules.All,
      Vector.empty
    )
    assert(derived.findings.exists(finding => finding.check == "agreement" && finding.resolution.isEmpty))
    assert(
      PTXOperandDomains.derive(Vector.empty, read(), PTXOperandRules.All, Vector.empty).findings
        .forall(_.check != "agreement")
    )
  }

  test("a type slot value no table lists is a finding an override resolves") {
    val definition = PTXOperandDomains.Definition(
      "Mystery",
      "mystery",
      Vector(
        PTXOperandDomains.FormDomains(
          Vector(PTXOperandDomains.Slot("type", Vector("s32", "q7"))),
          Vector.empty,
          Vector.empty,
          complete = true,
          Vector.empty
        )
      )
    )
    val open = PTXOperandDomains.derive(Vector(definition), read(), PTXOperandRules.All, Vector.empty)
    assert(open.findings.exists(finding =>
      finding.check == "slot-type" && finding.subject == ".q7" && finding.resolution.isEmpty
    ))
    val failure = intercept[IllegalStateException](GeneratePTXDomains.check(open))
    assert(failure.getMessage.contains(".q7"), failure.getMessage)
    val rules = PTXOperandRules.All.copy(overrides =
      Vector(PTXOperandRules.Override("slot-type", "q7", "a vendor type", None))
    )
    val closed = PTXOperandDomains.derive(Vector(definition), read(), rules, Vector.empty)
    assert(closed.findings.exists(finding => finding.subject == ".q7" && finding.resolution.contains("a vendor type")))
    assert(GeneratePTXDomains.unresolved(closed).forall(_.subject != ".q7"))
  }

  test("rows cite sections and sentences the document has") {
    val document = Jsoup.parse(
      """<section id="stack-manipulation-instructions-alloca"><p>It is an integer constant,
        |must be a power of 2 and must not exceed 2^23.</p></section>""".stripMargin
    )
    val failures = PTXOperandRules.verify(PTXOperandRules.All, document)
    assert(failures.exists(_.contains("section logic-and-shift-instructions-shl is absent")), failures)
    assert(!failures.exists(_.contains("stack-manipulation-instructions-alloca")), failures)
    val altered = Jsoup.parse("""<section id="stack-manipulation-instructions-alloca"><p>Other text.</p></section>""")
    assert(PTXOperandRules.verify(PTXOperandRules.All, altered).exists(_.contains("alloca does not say")))
  }

  test("modifier domains intern the slots of every form") {
    val slot = PTXOperandDomains.Slot("type", Vector("s32", "u32"))
    val form = PTXOperandDomains.FormDomains(
      Vector(slot),
      Vector(PTXOperandDomains.Slot("sat", Vector("sat"))),
      Vector.empty,
      complete = true,
      Vector(PTXOperandDomains.Operand("d", "R", optional = false))
    )
    val derived = PTXOperandDomains.derive(
      Vector(PTXOperandDomains.Definition("Add", "add", Vector(form, form))),
      read(),
      PTXOperandRules.All,
      Vector.empty
    )
    assertEquals(derived.namedSlots, Vector("type"))
    val output = GeneratePTXDomains.render(derived)
    assertEquals("\"type\",\"s32\",\"u32\"".r.findAllIn(output.modifiers).size, 1)
    assert(output.modifiers.contains("[\"Add\",\"add\""))
    assert(output.operands.contains("\"agreement\""))
    assert(output.report.contains("| instruction definitions | 1 |"))
  }
}
