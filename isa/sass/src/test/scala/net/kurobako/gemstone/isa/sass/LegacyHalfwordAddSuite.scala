package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

class LegacyHalfwordAddSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    sm                           <- Vec.nonEmpty(50, 60)
    (opcode, operands, expected) <- Vec.nonEmpty(
      (
        "IADD3.LS",
        "R4,R4.H1.reuse,R5.H0,R6.H0",
        Vec.tabulateNonEmpty(32) { lane =>
          val input = (0xfffffff0L + lane.raw) & 0xffffffffL
          ((((input >>> 16) + 65535L) << 16) + 0xabcdL).toInt
        }
      ),
      (
        "IADD3.RS",
        "R4,R4.H1,R5,R6.H0",
        Vec.tabulateNonEmpty(32) { lane =>
          val input = (0xfffffff0L + lane.raw) & 0xffffffffL
          ((((input >>> 16) + 0x1234ffffL) >>> 16) + 0xabcdL).toInt
        }
      ),
      (
        "VADD.U16.U16.MRG_16H",
        "R4,R4,R5,R6",
        Vec.tabulateNonEmpty(32) { lane =>
          val input = (0xfffffff0L + lane.raw) & 65535L
          (((input + 65535L) << 16) | 0xabcdL).toInt
        }
      )
    )
  }
    test(s"sm$sm $opcode selects and merges halfwords across carry boundaries") {
      executeSynthetic(
        Vec.nonEmpty(
          ("S2R", "R0,SR_TID.X", None),
          ("IADD32I", "R4,R0,0xfffffff0", None),
          ("MOV", "R5,0x1234ffff", None),
          ("MOV", "R6,0x8765abcd", None),
          (opcode, operands, None),
          ("LDC.64", "R2,c[0x0][0x160]", None),
          ("IMAD.WIDE.U32", "R2,R0,4,R2", None),
          ("STG.E", "[R2.64],R4", None),
          ("EXIT", "", None)
        ),
        expected,
        sm = sm
      )
    }
}
