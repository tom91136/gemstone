package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.either.*

class RangeReductionSuite extends munit.FunSuite {
  private def bits(value: Double): Int = java.lang.Float.floatToRawIntBits(value.toFloat)

  for {
    sm    <- Vec(35, 50, 60)
    input <- Vec(0.0f, -0.0f, 1.0f, -1.0f, Math.PI.toFloat, 1e10f)
  }
    test(s"sm_$sm reduction captures radians before aliases are overwritten: $input") {
      check(
        sm,
        s"LDC.64 R10, c[0x0][0x160]; MOV R4, 0x${bits(input.toDouble).toHexString}; RRO.SINCOS R4, R4; MUFU.SIN R5, R4; " +
          "MUFU.COS R4, R4; STG.E [R10], R5; STG.E [R10+4], R4; EXIT",
        Vec(bits(Math.sin(input.toDouble)), bits(Math.cos(input.toDouble)))
      )
    }

  for (
    (name, middle) <- Vec(
      "integer escape"         -> "MOV R5, R4",
      "integer min/max escape" -> "IMNMX R4, R4, R5, PT",
      "memory escape"          -> "STG.E [R10], R4",
      "implicit pair escape"   -> "DADD R6, R3, R8",
      "address pair escape"    -> "LDG.E R6, [R3]",
      "branch across lifetime" -> "BRA 0x30",
      "guarded consumer"       -> "@P0 MUFU.SIN R5, R4"
    )
  ) test(s"opaque range reduction rejects $name") {
    val function = Listing.program(s"RRO.SINCOS R4, R4; $middle; MUFU.COS R4, R4; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(35, function, fullLayout))
  }

  test("incoming control flow after the final consumer does not expose the opaque result") {
    val function = Listing.program("RRO.SINCOS R4, R4; MUFU.COS R6, R4; MOV R4, RZ; BRA 0x20").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  for (middle <- Vec("@P0 BPT.TRAP 0x1", "@P0 KILL", "YIELD"))
    test(s"a non-branching $middle does not split a range reduction lifetime") {
      val function = Listing.program(s"RRO.SINCOS R4, R4; $middle; MUFU.COS R4, R4; EXIT").value
      assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 32))
    }

  test("a branch cannot enter a fused reduction after its producer") {
    val function = Listing.program("BRA 0x20; RRO.SINCOS R4, R4; MUFU.COS R4, R4; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(35, function, fullLayout))
  }

  test("a wide load's address pair does not alias the reduced register through its width") {
    val function =
      Listing.program("RRO.SINCOS R4, R4; MUFU.SIN R5, R4; LDG.E.128 R8, [R2]; MUFU.COS R4, R4; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16, 48))
  }

  test("a shifted-add destination with a carry ends a consumed range reduction lifetime") {
    val function = Listing.program("RRO.SINCOS R4, R4; MUFU.COS R6, R4; ISCADD R4.CC, R5, R6, 0x4; BRA 0x20").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  for (sm <- Vec(35, 50, 60)) test(s"sm_$sm a wide load over the reduced register ends its lifetime") {
    check(
      sm,
      "LDC.64 R10, c[0x0][0x160]; MOV R7, 9; STG.E [R10+8], R7; MOV R4, 0; RRO.SINCOS R6, R4; MUFU.COS R5, R6; " +
        "STG.E [R10], R5; LD.E.128 R4, [R10]; STG.E [R10+4], R6; EXIT",
      Vec(0x3f800000, 9, 9, 0)
    )
  }

  for (sm <- Vec(50, 60)) test(s"sm_$sm integer min/max ends a consumed range reduction lifetime") {
    check(
      sm,
      "LDC.64 R10, c[0x0][0x160]; MOV R4, 0; MOV R5, 2; RRO.SINCOS R4, R4; MUFU.COS R6, R4; IMNMX R4, R5, 3, PT; " +
        "STG.E [R10], R6; STG.E [R10+4], R4; EXIT",
      Vec(0x3f800000, 2)
    )
  }
}
