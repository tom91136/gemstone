package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class UniformReductionSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    sm                    <- Vec.nonEmpty(100, 103)
    (operation, expected) <- Vec.nonEmpty("MIN" -> 0, "MAX" -> -1, "MIN.S32" -> -16, "MAX.S32" -> 15)
  }
    test(s"sm$sm CREDUX.$operation reduces varying lanes into a uniform register") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD3", "R4,R0,-16,RZ", None),
          (s"CREDUX.$operation", "UR6,R4", None),
          ("MOV", "R4,UR6", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(_ => expected),
        sm = sm
      )
    }
  for (sm <- Vec.nonEmpty(100, 103))
    test(s"sm$sm VIMNMX accepts discarded predicate outputs") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("VIMNMX", "R4,PT,PT,R0,15,PT", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => math.min(lane.raw, 15)),
        sm = sm
      )
    }
  for (sm <- Vec.nonEmpty(100, 103))
    test(s"sm$sm REDUX defaults to bitwise AND with a uniform destination") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD3", "R4,R0,32,RZ", None),
          ("REDUX", "UR6,R4", None),
          ("MOV", "R4,UR6", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(_ => 32),
        sm = sm
      )
    }
}
