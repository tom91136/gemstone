package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*

class CountedBarrierSuite extends SyntheticSASSExecutionSuiteBase {
  test("register-counted block barriers synchronize participants without waiting for exited lanes") {
    executeSynthetic(
      body = Vec.nonEmpty(
        ("S2R", "R0,SR_TID.X", None),
        ("ISETP.GE.U32.AND", "P0,PT,R0,32,PT", None),
        ("EXIT", "", Some("@P0")),
        ("MOV", "R36,1", None),
        ("MOV", "R37,32", None),
        ("STS", "[R0.X4],R0", None),
        ("BAR.SYNC.DEFER_BLOCKING", "R36,R37", None),
        ("LOP3.LUT", "R6,R0,1,RZ,0x3c,!PT", None),
        ("LDS", "R4,[R6.X4]", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("IMAD.WIDE.U32", "R10,R0,4,R10", None),
        ("STG.E", "[R10],R4", None),
        ("EXIT", "", None)
      ),
      expected = Vec.tabulateNonEmpty(32)(lane => lane.raw ^ 1).concatNonEmpty(Vec.tabulateNonEmpty(32)(_ => 0)),
      shared = 128,
      sm = 100
    )
  }
}
