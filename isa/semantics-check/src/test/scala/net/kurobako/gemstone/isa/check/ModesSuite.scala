package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.isa.check.Modes.Mode
import net.kurobako.gemstone.testing.*

class ModesSuite extends Suite {
  private val sass   = SASSGenerator.describe("sm_75")
  private val amdgpu =
    AMDGPUGenerator.cases(Set("v_add_f32", "v_add_u32", "v_fma_f32").contains)

  private def sassCase(instance: String): Case =
    sass.find(_.instance == instance).getOrElse(fail(s"no $instance"))

  private def amdCase(instance: String): Case =
    amdgpu.find(_.instance == instance).getOrElse(fail(s"no $instance"))

  /** The instruction `instance`, rendered as `kase`'s definition does. */
  private def as(kase: Case, instance: String): Case =
    sassCase(instance).copy(definition = kase.definition)

  test("a plain floating-point sum has no mode to render") {
    assertEquals(Modes.check(sassCase("FADD R0, R4, R8")), Right(()))
  }

  test("a flushed sum renders its flush") {
    assertEquals(Modes.check(sassCase("FADD.FTZ R0, R4, R8")), Right(()))
  }

  test("a rounded sum renders its rounding") {
    assertEquals(Modes.check(sassCase("FADD.RM R0, R4, R8")), Right(()))
  }

  test("a saturated sum renders its saturation") {
    assertEquals(Modes.check(sassCase("FADD.SAT R0, R4, R8")), Right(()))
  }

  test("a flush the definition does not show is named with the instruction") {
    val plain = sassCase("FADD R0, R4, R8")
    Modes.check(as(plain, "FADD.FTZ R0, R4, R8")) match {
      case Left(reason) =>
        assert(reason.contains("FADD.FTZ R0, R4, R8"), reason)
        assert(reason.contains(Mode.Flush.label), reason)
      case Right(_) => fail("expected the missing flush to be refused")
    }
  }

  test("a rounding the definition does not show is named with the instruction") {
    val plain = sassCase("FADD R0, R4, R8")
    Modes.check(as(plain, "FADD.RM R0, R4, R8")) match {
      case Left(reason) =>
        assert(reason.contains("FADD.RM R0, R4, R8"), reason)
        assert(reason.contains(Mode.Rounding.label), reason)
      case Right(_) => fail("expected the missing rounding to be refused")
    }
  }

  test("a saturation the definition does not show is named with the instruction") {
    val plain = sassCase("FADD R0, R4, R8")
    Modes.check(as(plain, "FADD.SAT R0, R4, R8")) match {
      case Left(reason) =>
        assert(reason.contains("FADD.SAT R0, R4, R8"), reason)
        assert(reason.contains(Mode.Saturate.label), reason)
      case Right(_) => fail("expected the missing saturation to be refused")
    }
  }

  test("an AMDGPU sum and fused multiply-add render the rounding and denormal fields of MODE") {
    assertEquals(Modes.check(amdCase("v_add_f32_e32 v0, v1, v2")), Right(()))
    assertEquals(Modes.check(amdCase("v_fma_f32_e64 v0, v1, v2, v3")), Right(()))
  }

  test("an AMDGPU integer sum has no floating-point mode") {
    assertEquals(Modes.check(amdCase("v_add_u32_e32 v0, v1, v2")), Right(()))
  }
}
