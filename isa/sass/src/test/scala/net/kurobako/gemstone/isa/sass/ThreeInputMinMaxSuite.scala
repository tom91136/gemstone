package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

class ThreeInputMinMaxSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    sm                             <- Vec.nonEmpty(100, 103)
    (modifier, selector, expected) <- Vec.nonEmpty(
      ("", "PT", Int.MinValue),
      ("", "!PT", Int.MaxValue),
      (".U32", "PT", Int.MaxValue),
      (".U32", "!PT", -1)
    )
  }
    test(s"sm$sm VIMNMX3$modifier selector=$selector retains signed boundaries and an aliased third source") {
      executeSynthetic(
        Vec.nonEmpty(
          ("MOV", "R4,0x80000000", None),
          ("UMOV", "UR6,0x7fffffff", None),
          ("MOV", "R5,0xffffffff", None),
          (s"VIMNMX3$modifier", s"R5,R4,UR6,R5,$selector", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("STG.E", "[R2.64],R5", None),
          ("EXIT", "", None)
        ),
        Vec.nonEmpty(expected),
        sm = sm
      )
    }
}
