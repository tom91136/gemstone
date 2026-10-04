package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class LegacyIdentityFlagsSuite extends SyntheticSASSExecutionSuiteBase {
  for (sm <- Vec.nonEmpty(35, 50, 60))
    test(s"sm$sm identity conversion supplies the low-word equality flag") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("I2I.U32.U32", "RZ.CC,R0", None),
          ("ISETP.NE.X.AND", "P0,PT,RZ,RZ,PT", None),
          ("MOV", "R5,11", None),
          ("SEL", "R4,R5,22,P0", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => if (lane.raw == 0) 22 else 11),
        sm = sm
      )
    }
  for {
    sm        <- Vec.nonEmpty(50, 60)
    condition <- Vec.nonEmpty("Z", "NZ")
  }
    test(s"sm$sm LOP.AND.X.$condition combines low and high zero tests") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("LOP.AND", "R4,R0,1", None),
          ("LOP.AND", "R5,R0,2", None),
          ("LOP.OR", "R5,R5,0x10000", None),
          ("I2I.U32.U32", "RZ.CC,R4", None),
          (s"LOP.AND.X.$condition", "P0,R5,R5,0xffff", None),
          ("SEL", "R4,RZ,1,P0", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => if (((lane.raw & 3) == 0) == (condition == "Z")) 0 else 1),
        sm = sm
      )
    }
}
