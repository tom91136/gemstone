package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

class FloatingMinMaxSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    propagate <- Vec.nonEmpty(false, true)
    minimum   <- Vec.nonEmpty(false, true)
  } test(s"floating min/max NaN propagation=$propagate minimum=$minimum") {
    for (
      (a, b) <- Vec.nonEmpty(
        (Float.NaN, 2.0f),
        (2.0f, Float.NaN),
        (Float.NaN, Float.NaN),
        (-0.0f, 0.0f),
        (3.0f, -2.0f),
        (Float.PositiveInfinity, 2.0f)
      )
    ) {
      val numeric = if (a.isNaN) b else if (b.isNaN) a else if (minimum) Math.min(a, b) else Math.max(a, b)
      // SASS emits the canonical all-mantissa-bits NaN.
      val expected = if (numeric.isNaN || (propagate && (a.isNaN || b.isNaN))) 0x7fffffff
      else java.lang.Float.floatToRawIntBits(numeric)
      executeSynthetic(
        Vec.nonEmpty(
          ("LDC.64", "R10,c[0x0][0x160]", None),
          ("MOV", s"R4,0x${java.lang.Float.floatToRawIntBits(a).toHexString}", None),
          ("UMOV", s"UR6,0x${java.lang.Float.floatToRawIntBits(b).toHexString}", None),
          (if (propagate) "FMNMX.NAN" else "FMNMX", s"R4,R4,UR6,${if (minimum) "PT" else "!PT"}", None),
          ("STG.E", "[R10],R4", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(expected),
        sm = 90
      )
    }
  }
}
