package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*
import scala.concurrent.duration.*

class EquivalenceGoldenSuite extends Suite {
  private def definition(text: String, operands: (String, String)*)(using munit.Location): Definition =
    InstructionSemantics.describe(Target("gfx1030"), text, operands.toMap, Vec.from(operands.map(_._2)))
      .fold(error => fail(s"$text: ${error.message}"), identity)

  private def compare(left: String, right: String, operands: (String, String)*)(using munit.Location): Verdict = {
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")
    Bitwuzla.equivalence(definition(left, operands*), definition(right, operands*), memory = false, 10.seconds)
  }

  private val registers = Vector("v0" -> "VDST", "v1" -> "SRC0", "v2" -> "VSRC1")

  test("reordered 32-bit additions are equal") {
    assertEquals(
      compare("v_add_nc_u32_e32 v0, v1, v2", "v_add_nc_u32_e32 v0, v2, v1", registers*),
      Verdict.Equal
    )
  }

  test("a left shift by one and a self-add are equal") {
    assertEquals(
      compare("v_lshlrev_b32_e32 v0, 1, v1", "v_add_nc_u32_e32 v0, v1, v1", "v0" -> "VDST", "v1" -> "VSRC1"),
      Verdict.Equal
    )
  }

  test("a 32-bit addition and subtraction are different") {
    compare("v_add_nc_u32_e32 v0, v1, v2", "v_sub_nc_u32_e32 v0, v1, v2", registers*) match {
      case Verdict.Different(counterexample) => assert(counterexample.nonEmpty, "empty counterexample")
      case other                             => fail(s"expected Different, got $other")
    }
  }

  test("an addition of two sources and of one source twice are different") {
    compare("v_add_nc_u32_e32 v0, v1, v2", "v_add_nc_u32_e32 v0, v1, v1", registers*) match {
      case Verdict.Different(counterexample) => assert(counterexample.nonEmpty, "empty counterexample")
      case other                             => fail(s"expected Different, got $other")
    }
  }
}
