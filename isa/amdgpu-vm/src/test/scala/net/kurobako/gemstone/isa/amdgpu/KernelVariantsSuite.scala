package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.either.*
import scala.collection.mutable

class KernelVariantsSuite extends munit.FunSuite {
  test("a usable architecture variant does not inspect the generic kernel body") {
    val target    = "wrapped_target_archE1030E"
    val generic   = "wrapped_target_archE4294967295E"
    val inspected = mutable.ArrayBuffer.empty[String]
    val aliases   = KernelVariants.select(
      Set(target, generic),
      "gfx1030",
      name => {
        inspected += name
        true
      }
    ).value
    assertEquals(aliases.values.toSet, Set(target))
    assertEquals(inspected.toSeq, Seq(target))
  }

  test("gfx90a selects rocPRIM's decimal architecture enum instead of a generic stub") {
    val selected = "wrapped_target_archE910E"
    val generic  = "wrapped_target_archE4294967295E"
    val other    = "wrapped_target_archE906E"
    val aliases  = KernelVariants.select(Set(selected, generic, other), "gfx90a", _ == selected).value
    assertEquals(aliases.values.toSet, Set(selected))
  }

  test("AMDGPU kernel aliases select the indexed implementation matching the code-object target") {
    val generic = "wrapped_target_archE"
    val gfx900  = "wrapped_target_archE900E"
    val gfx906  = "wrapped_target_archE906E"
    val aliases = KernelVariants.select(Set(generic, gfx900, gfx906), "gfx900", _ => true).value

    assertEquals(aliases(generic), gfx900)
    assertEquals(aliases(gfx900), gfx900)
    assertEquals(aliases(gfx906), gfx900)
  }

  test("kernel aliases prefer usable exact revisions before family and generic bodies") {
    val generic = "wrapped_target_archE"
    val family  = "wrapped_target_archE1030E"
    val exact   = "wrapped_target_archE1036E"
    val names   = Set(generic, family, exact)
    assertEquals(KernelVariants.select(names, "gfx1036", _ => true).value(generic), exact)
    assertEquals(KernelVariants.select(names, "gfx1036", _ != exact).value(generic), family)
    assertEquals(KernelVariants.select(Set(generic, family), "gfx1036", _ => true).value(generic), family)
    assertEquals(KernelVariants.select(names, "gfx1036", _ == generic).value(exact), generic)
  }

  test("AMDGPU kernel aliases preserve unrelated symbols") {
    val aliases = KernelVariants.select(Set("first", "second"), "gfx900", _ => true).value

    assertEquals(aliases, Map("first" -> "first", "second" -> "second"))
  }

  test("AMDGPU kernel aliases fall back to a stub but reject a group without a target or generic body") {
    val generic = "wrapped_target_archE"
    val gfx906  = "wrapped_target_archE906E"

    assertEquals(KernelVariants.select(Set(generic, gfx906), "gfx900", _ => false).value(gfx906), generic)
    assertEquals(
      KernelVariants.select(Set(gfx906), "gfx900", _ => true),
      Left[NonEmpty[String], Nothing]("AMDGPU kernel variant wrapped_target_archE has no gfx900 implementation")
    )
  }
}
