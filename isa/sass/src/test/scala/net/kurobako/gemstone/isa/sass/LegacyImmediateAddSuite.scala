package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class LegacyImmediateAddSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    sm                  <- Vec.nonEmpty(50, 60)
    (immediate, signed) <-
      Vec.nonEmpty("0x7ffff" -> 524287, "0x80000" -> -524288, "0xffaff" -> -1281, "-0x501" -> -1281, "0xfffff" -> -1)
  }
    test(s"sm$sm plain IADD3 sign-extends $immediate and retains an aliased addend") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("MOV", "R4,0x501", None),
          ("IADD3", s"R4,-R0,$immediate,R4", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => 1281 + signed - lane.raw),
        sm = sm
      )
    }
}
