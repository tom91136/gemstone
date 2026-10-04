package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class UniformBitMaskSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    (start, count, expected) <- Vec.nonEmpty((0, 0, 0), (0, 32, -1), (4, 5, 0x1f0), (31, 2, Int.MinValue), (32, 1, 0))
    immediate                <- Vec.nonEmpty(false, true)
  }
    test(s"UBMSK start=$start count=$count immediate=$immediate preserves an aliased width source") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("UMOV", s"UR4,$start", None),
          ("UMOV", s"UR5,$count", None),
          ("UBMSK", if (immediate) s"UR5,UR4,$count" else "UR5,UR4,UR5", None),
          ("MOV", "R4,UR5", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(_ => expected),
        sm = 120
      )
    }
}
