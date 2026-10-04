package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class UniformOnlyAddressSuite extends SyntheticSASSExecutionSuiteBase {
  for (address <- Vec.nonEmpty("[RZ.U32+UR8]", "[UR8.U32]"))
    test(s"Blackwell U32 address $address keeps the full uniform pointer") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("LDCU.64", "UR8,c[0x0][0x160]", None),
          ("ISETP.EQ.U32.AND", "P0,PT,R0,0,PT", None),
          ("MOV", "R4,47", None),
          ("MOV", "R5,89", None),
          ("STG.E.64.STRONG.GPU", s"$address,R4", Some("@P0")),
          ("BAR.SYNC", "0", None),
          ("LDG.E.64", s"R4,$address", None),
          ("IADD", "R4,R4,R5", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(_ => 136),
        sm = 120
      )
    }
}
