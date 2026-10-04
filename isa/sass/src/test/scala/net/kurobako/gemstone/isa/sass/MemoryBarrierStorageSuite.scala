package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

class MemoryBarrierStorageSuite extends SyntheticSASSExecutionSuiteBase {
  test("stored TRANS64 initialization and same-value rewrite reset the barrier phase") {
    executeSynthetic(
      body = Vec.nonEmpty(
        // ptxas TRANS64 initialization for one arrival: q = 2^20 - 1; words are q<<1, q<<11.
        ("MOV", "R4,0x1ffffe", None),
        ("MOV", "R5,0x7ffff800", None),
        ("STS.64", "[16],R4", None),
        ("FENCE.VIEW.ASYNC.S", "", None),
        ("SYNCS.CCTL.IVALL", "", None),
        ("SYNCS.ARRIVE.TRANS64.A1T0", "RZ,[16],RZ", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P0,[16],RZ", None),
        ("MOV", "R6,1", None),
        ("SEL", "R6,R6,RZ,P0", None),
        ("STS.64", "[16],R4", None),
        ("FENCE.VIEW.ASYNC.S", "", None),
        ("SYNCS.CCTL.IVALL", "", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P0,[16],RZ", None),
        ("SEL", "R6,RZ,R6,P0", None),
        ("SYNCS.ARRIVE.TRANS64.A1T0", "RZ,[16],RZ", None),
        ("SYNCS.PHASECHK.TRANS64.TRYWAIT", "P0,[16],RZ", None),
        ("SEL", "R6,R6,RZ,P0", None),
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("STG.E", "[R10],R6", None),
        ("EXIT", "", None)
      ),
      expected = Vec.nonEmpty(1),
      shared = 24,
      sm = 100
    )
  }
}
