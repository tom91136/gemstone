package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.Harness.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class RangeReductionSuite extends Suite {
  private def bits(value: Double): Int = java.lang.Float.floatToRawIntBits(value.toFloat)

  for {
    sm    <- Vec(35, 50, 60)
    input <- Vec(0.0f, -0.0f, 1.0f, -1.0f, Math.PI.toFloat, 1e10f)
  }
    test(s"sm_$sm reduction captures radians before aliases are overwritten: $input") {
      // the unit's own approximations, which HardwareTableSuite holds to the hardware's results
      val radians = bits(input.toDouble).toLong & 0xffffffffL
      check(
        sm,
        s"LDC.64 R10, c[0x0][0x160]; MOV R4, 0x${bits(input.toDouble).toHexString}; RRO.SINCOS R4, R4; MUFU.SIN R5, R4; " +
          "MUFU.COS R4, R4; STG.E [R10], R5; STG.E [R10+4], R4; EXIT",
        Vec(Mufu.sine(radians, VMIR.MufuFlavour.Legacy).toInt, Mufu.cosine(radians, VMIR.MufuFlavour.Legacy).toInt)
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

  for (indirect <- Vec("BRX R6, -0x10", "BRXU UR4, -0x10", "JMX R0, 0x40"))
    test(s"legacy range reduction rejects register-indirect control flow $indirect") {
      val function = Listing.program(s"RRO.SINCOS R4, R4; MUFU.COS R4, R4; $indirect; EXIT").value
      val error    = intercept[IllegalArgumentException](LegacyRangeReduction.inputs(35, function, fullLayout))
      assertEquals(error.getMessage, "legacy range reduction has indirect control flow")
    }

  test("a legacy sync marker on a data instruction does not split a range reduction lifetime") {
    val function = Listing.program("SSY 0x30; RRO.SINCOS R4, R4; FFMA.S R5, R6, R6, R7; MUFU.COS R4, R4; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(35, function, fullLayout).inputs.keySet, Set(16, 48))
  }

  test("a transfer after the final consumer ends a range reduction lifetime when the register is dead") {
    val function = Listing.program("PBK 0x40; RRO.EX2 R4, R4; MUFU.EX2 R5, R4; @!P0 BRK; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(16, 32))
  }

  test("a transfer after the final consumer rejects a later read of the reduced register") {
    val function = Listing.program("PBK 0x50; RRO.EX2 R4, R4; MUFU.EX2 R5, R4; @!P0 BRK; FMUL R6, R4, R4; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
  }

  private def joined(afterJoin: String): String =
    "PBK 0x70; RRO.EX2 R4, R4; MUFU.EX2 R5, R4; @!P0 BRK; @!P1 BRA 0x60; MOV32I R4, 0x1; BRK; " +
      s"$afterJoin; FADD R6, R4, R4; EXIT"

  test("a break edge that skips the redefinition still ends the lifetime when the join redefines the register") {
    val function = Listing.program(joined("MOV32I R4, 0x7")).value
    assertEquals(LegacyRangeReduction.inputs(35, function, fullLayout).inputs.keySet, Set(16, 32))
  }

  test("a break edge that skips the redefinition rejects a join that reads the reduced register") {
    val function = Listing.program(joined("MOV32I R7, 0x7")).value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(35, function, fullLayout))
  }

  test("a consumer reachable around its reduction is rejected") {
    val function = Listing.program("MOV R4, RZ; @P0 BRA 0x30; RRO.EX2 R4, R4; MUFU.EX2 R5, R4; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(35, function, fullLayout))
  }

  test("two reductions reaching one consumer are rejected") {
    val function = Listing.program(
      "@P0 BRA 0x30; RRO.EX2 R4, R4; BRA 0x40; RRO.EX2 R4, R6; MUFU.EX2 R5, R4; EXIT"
    ).value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(35, function, fullLayout))
  }

  test("a guarded redefinition does not end a range reduction lifetime") {
    val function = Listing.program("RRO.SINCOS R4, R4; MUFU.SIN R5, R4; @P0 MOV R4, RZ; MOV R6, R5; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  test("a guarded redefinition rejects a later read of the reduced register") {
    val function = Listing.program("RRO.SINCOS R4, R4; MUFU.SIN R5, R4; @P0 MOV R4, RZ; MUFU.COS R6, R4; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
  }

  test("a read under the same guard as a redefinition is accepted") {
    val function =
      Listing.program("RRO.SINCOS R4, R4; MUFU.SIN R5, R4; @P0 MOV R4, RZ; @P0 FADD R6, R4, R4; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  test("a select reading the guard predicate does not rewrite it") {
    val function = Listing.program(
      "RRO.EX2 R4, R4; MUFU.EX2 R5, R4; @P0 MOV R4, RZ; SEL R6, R7, R8, !P0; @P0 FADD R9, R4, R4; EXIT"
    ).value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  test("a read under a different guard than a redefinition is rejected") {
    val function =
      Listing.program("RRO.SINCOS R4, R4; MUFU.SIN R5, R4; @P0 MOV R4, RZ; @!P0 FADD R6, R4, R4; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
  }

  test("a predicate rewrite between a guarded redefinition and its read is rejected") {
    val function = Listing.program(
      "RRO.SINCOS R4, R4; MUFU.SIN R5, R4; @P0 MOV R4, RZ; FSETP.GE.AND P0, PT, R7, RZ, PT; @P0 FADD R6, R4, R4; EXIT"
    ).value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
  }

  test("a predicate rewrite after a guarded redefinition is accepted once the register is overwritten") {
    val function = Listing.program(
      "RRO.SINCOS R4, R4; MUFU.SIN R5, R4; @P0 MOV R4, RZ; FSETP.GE.AND P0, PT, R7, RZ, PT; MOV R4, RZ; FADD R6, R4, R4; EXIT"
    ).value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  private def guardedBreak(guard: String): String =
    s"PBK 0x70; RRO.EX2 R4, R4; MUFU.EX2 R5, R4; @P0 MOV R4, RZ; $guard BRK; MOV R4, RZ; BRK; FADD R6, R4, R4; EXIT"

  test("a break guarded by the redefinition's predicate leaves the redefined register behind") {
    val function = Listing.program(guardedBreak("@P0")).value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(16, 32))
  }

  for (guard <- Vec("@!P0", "@P1"))
    test(s"a break guarded by $guard does not imply the redefinition") {
      val function = Listing.program(guardedBreak(guard)).value
      intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
    }

  test("a wide load's address pair does not alias the reduced register through its width") {
    val function =
      Listing.program("RRO.SINCOS R4, R4; MUFU.SIN R5, R4; LDG.E.128 R8, [R2]; MUFU.COS R4, R4; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16, 48))
  }

  test("a 64-bit store does not alias a reduced register above its pair") {
    val function = Listing.program("RRO.EX2 R11, R11; STG.E.64 [R12], R8; MUFU.EX2 R5, R11; EXIT").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 32))
  }

  test("a 64-bit store aliases a reduced register inside its pair") {
    val function = Listing.program("RRO.EX2 R9, R9; STG.E.64 [R12], R8; MUFU.EX2 R5, R9; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
  }

  test("a 128-bit store aliases a reduced register inside its quad") {
    val function = Listing.program("RRO.EX2 R11, R11; STG.E.128 [R12], R8; MUFU.EX2 R5, R11; EXIT").value
    intercept[IllegalArgumentException](LegacyRangeReduction.inputs(50, function, fullLayout))
  }

  test("a shifted-add destination with a carry ends a consumed range reduction lifetime") {
    val function = Listing.program("RRO.SINCOS R4, R4; MUFU.COS R6, R4; ISCADD R4.CC, R5, R6, 0x4; BRA 0x20").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  test("a high shifted-add destination ends a consumed range reduction lifetime") {
    val function = Listing.program("RRO.EX2 R4, R4; MUFU.EX2 R6, R4; LEA.HI R4, R6, R5, RZ, 0x9; BRA 0x20").value
    assertEquals(LegacyRangeReduction.inputs(50, function, fullLayout).inputs.keySet, Set(0, 16))
  }

  test("a compare with a scalar destination ends a consumed range reduction lifetime") {
    val function = Listing.program("RRO.SINCOS R4, R4; MUFU.COS R6, R4; ICMP.EQ R4, R6, R5, R0; BRA 0x20").value
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
