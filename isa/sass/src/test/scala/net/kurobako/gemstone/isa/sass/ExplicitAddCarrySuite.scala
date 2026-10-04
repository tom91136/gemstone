package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class ExplicitAddCarrySuite extends SyntheticSASSExecutionSuiteBase {
  for (
    (left, right, expected) <- Vec.nonEmpty(
      ("R0", "0xfffffff0", Vec.tabulateNonEmpty(32)(lane => if (lane.raw >= 16) 1 else 0)),
      ("R9", "-R0", Vec.tabulateNonEmpty(32)(lane => if (lane.raw <= 16) 1 else 0)),
      ("-R0", "16", Vec.tabulateNonEmpty(32)(lane => if (lane.raw <= 16) 1 else 0)),
      ("RZ", "-R0", Vec.tabulateNonEmpty(32)(lane => if (lane.raw == 0) 1 else 0))
    )
  )
    test(s"Blackwell IADD explicit carry for $left plus $right") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("MOV", "R9,16", None),
          ("IADD", s"R8,P0,$left,$right", None),
          ("MOV", "R8,1", None),
          ("SEL", "R4,R8,0,P0", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        expected,
        sm = 120
      )
    }
  for {
    predicate  <- Vec.nonEmpty("P0", "!P0")
    complement <- Vec.nonEmpty(false, true)
  }
    test(s"Blackwell IADD.X reads $predicate independently of hidden carry, complement=$complement") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD", "R8,P0,R0,0xfffffff0", None),
          ("IADD", "R8,P1,RZ,RZ", None),
          ("MOV", "R4,R0", None),
          ("IADD.X", if (complement) s"R4,RZ,~R4,$predicate" else s"R4,R4,10,$predicate", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32) { lane =>
          val carry = (lane.raw >= 16) != (predicate == "!P0")
          (if (complement) ~lane.raw else lane.raw + 10) + (if (carry) 1 else 0)
        },
        sm = 120
      )
    }
}
