package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class LegacyMultiplyAddFlagsSuite extends SyntheticSASSExecutionSuiteBase {
  for (
    (comparison, expected) <- Vec.nonEmpty(
      "GT" -> Vec.tabulateNonEmpty(32)(lane => if (16 > lane.raw) 1 else 0),
      "GE" -> Vec.tabulateNonEmpty(32)(lane => if (16 >= lane.raw) 1 else 0),
      "LT" -> Vec.tabulateNonEmpty(32)(lane => if (16 < lane.raw) 1 else 0),
      "LE" -> Vec.tabulateNonEmpty(32)(lane => if (16 <= lane.raw) 1 else 0),
      "EQ" -> Vec.tabulateNonEmpty(32)(lane => if (16 == lane.raw) 1 else 0),
      "NE" -> Vec.tabulateNonEmpty(32)(lane => if (16 != lane.raw) 1 else 0)
    )
  )
    test(s"Kepler IMAD with a negated addend feeds extended $comparison bounds checks") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("MOV", "R4,4", None),
          ("IMAD.U32.U32", "RZ.CC,R4,4,-R0", None),
          (s"ISETP.$comparison.U32.X.AND", "P0,PT,RZ,RZ,PT", None),
          ("MOV", "R8,1", None),
          ("SEL", "R4,R8,0,P0", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        expected,
        sm = 35
      )
    }
  for {
    sm        <- Vec.nonEmpty(35, 50)
    immediate <- Vec.nonEmpty(-1, -2, -17)
  }
    test(s"sm$sm extended addition preserves negative immediate $immediate") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD32I", "R4.CC,-R0,16", None),
          ("IADD32I.X", s"R4,RZ,$immediate", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => immediate + (if (lane.raw <= 16) 1 else 0)),
        sm = sm
      )
    }
  for {
    sm     <- Vec.nonEmpty(50, 60)
    addend <- Vec.nonEmpty(0L, 1L, 0xfffffff0L)
  }
    test(s"sm$sm XMAD condition codes carry into the next word with addend $addend") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("MOV", s"R4,$addend", None),
          ("MOV", "R5,65535", None),
          ("XMAD", "R6.CC,R0,R5,R4", None),
          ("IADD.X", "R4,RZ,RZ", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => ((lane.raw.toLong * 65535L + addend) >>> 32).toInt),
        sm = sm
      )
    }
  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm$sm XMAD.X consumes carry without losing the selected halfword") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("MOV", "R5,0x30007", None),
          ("MOV", "R4,11", None),
          ("IADD32I", "R6.CC,R0,0xfffffff0", None),
          ("XMAD.X", "R4,R5.H1,7,R4", None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        Vec.tabulateNonEmpty(32)(lane => 32 + (if (lane.raw >= 16) 1 else 0)),
        sm = sm
      )
    }
}
