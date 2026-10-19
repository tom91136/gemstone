package net.kurobako.gemstone.isa.amdgpu

import hardwareGolden.*
import hardwareGoldenTables.*
import net.kurobako.gemstone.collection.*

class HardwareGoldenSuite extends munit.FunSuite {
  private val gfx906  = Target("gfx906")
  private val gfx803  = Target("gfx803")
  private val gfx1036 = Target("gfx1036")

  for {
    (target, wave32, probe, table) <- Vec(
      (gfx906, false, "h_dot", dotGfx906),
      (gfx906, false, "h_packed_half", packedGfx906),
      (gfx803, false, "h_packed_half", packedGfx803),
      (gfx1036, true, "h_packed_half", packedGfx1036)
    )
    golden <- table
  }
    test(s"$target hardware: $probe ${golden.op}: ${golden.form}") {
      check(target, wave32, golden)
    }

  for {
    (target, probe, lanes, table) <- Vec(
      (gfx906, "h_dpp", dppGfx906Lanes, dppGfx906),
      (gfx906, "h_swizzle", swizzleGfx906Lanes, swizzleGfx906),
      (gfx803, "h_dpp", dppGfx803Lanes, dppGfx803),
      (gfx803, "h_swizzle", swizzleGfx803Lanes, swizzleGfx803)
    )
    op <- table
  }
    test(s"$target hardware: $probe ${op.op}: ${op.form}") {
      check(target, lanes, op)
    }
}
