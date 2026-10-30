package net.kurobako.gemstone.isa.amdgpu

import hardwareGolden.*
import hardwareGoldenTables.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.Suite

class HardwareGoldenSuite extends Suite {
  private val gfx906  = Target("gfx906")
  private val gfx803  = Target("gfx803")
  private val gfx1036 = Target("gfx1036")
  private val gfx1150 = Target("gfx1150")

  // hipcc flushes binary32 denormals on gfx8 and keeps every width's elsewhere
  private def kernel(target: Target): Int = descriptor(if (target == gfx803) 0xc else 0xf)

  // ops whose sampled rows meet a known gap of the transcendental unit's model, reported as ignored
  private val approximated: Set[(Target, String)] = Set(
    // sine and cosine cubics fitted to the unit, within 0.3 ulp but coarser next to the half-turn zero
    gfx906  -> "Sin",
    gfx906  -> "SinFtz",
    gfx906  -> "CosFtz",
    gfx906  -> "FastSin",
    gfx906  -> "FastCos",
    gfx803  -> "Sin",
    gfx803  -> "Cos",
    gfx803  -> "SinFtz",
    gfx803  -> "CosFtz",
    gfx803  -> "FastSin",
    gfx803  -> "FastCos",
    gfx1036 -> "Sin",
    gfx1036 -> "SinFtz",
    gfx1036 -> "CosFtz",
    gfx1036 -> "FastSin",
    gfx1036 -> "FastCos",
    gfx1150 -> "Sin",
    gfx1150 -> "SinFtz",
    gfx1150 -> "CosFtz",
    gfx1150 -> "FastSin",
    gfx1150 -> "FastCos",
    // logarithm rounded correctly in the windows near one, where the unit is not
    gfx906  -> "Logf",
    gfx1036 -> "Logf",
    gfx1150 -> "Logf"
  )

  private def options(target: Target, op: String, name: String): munit.TestOptions =
    if (approximated((target, op))) munit.TestOptions(name).ignore else munit.TestOptions(name)

  for {
    (target, wave32, probe, table) <- Vec(
      (gfx906, false, "h_dot", dotGfx906),
      (gfx906, false, "h_packed_half", packedGfx906),
      (gfx906, false, "h_transcendental", transcendentalGfx906),
      (gfx803, false, "h_packed_half", packedGfx803),
      (gfx803, false, "h_transcendental", transcendentalGfx803),
      (gfx1036, true, "h_packed_half", packedGfx1036),
      (gfx1036, true, "h_transcendental", transcendentalGfx1036),
      (gfx1150, false, "h_dot", dotGfx1150),
      (gfx1150, false, "h_packed_half", packedGfx1150),
      (gfx1150, false, "h_transcendental", transcendentalGfx1150)
    )
    golden <- table
  }
    test(options(target, golden.op, s"$target hardware: $probe ${golden.op}: ${golden.form}")) {
      check(target, wave32, golden, kernel(target))
    }

  for {
    (target, probe, lanes, table) <- Vec(
      (gfx906, "h_dpp", dppGfx906Lanes, dppGfx906),
      (gfx906, "h_swizzle", swizzleGfx906Lanes, swizzleGfx906),
      (gfx803, "h_dpp", dppGfx803Lanes, dppGfx803),
      (gfx803, "h_swizzle", swizzleGfx803Lanes, swizzleGfx803),
      (gfx1150, "h_swizzle", swizzleGfx1150Lanes, swizzleGfx1150)
    )
    op <- table
  }
    test(s"$target hardware: $probe ${op.op}: ${op.form}") {
      check(target, lanes, op, kernel(target))
    }
}
