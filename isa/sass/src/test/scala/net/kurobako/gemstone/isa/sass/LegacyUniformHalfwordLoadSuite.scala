package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class LegacyUniformHalfwordLoadSuite extends SyntheticSASSExecutionSuiteBase {
  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm$sm LDS.U.16 zero-extends shared halfwords and ignores adjacent bytes") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("SHL", "R1,R0,2", None),
          ("IADD32I", "R4,R0,0xdead8000", None),
          ("STS", "[R1],R4", None),
          ("MEMBAR.GL.IVALLD", "", None),
          ("LDS.U.16", "R4,[R1]", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => 0x8000 + lane.raw),
        shared = 128,
        sm = sm
      )
    }
}
