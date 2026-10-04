package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*

class ClangConformanceSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    sm    <- Vec.nonEmpty(35, 50, 60)
    index <- Vec.nonEmpty(0, 1, -1, Int.MinValue, Int.MaxValue, 0x800001)
  } test(s"sm_$sm negated ISCADD scales before subtraction and produces no-borrow for index $index") {
    val shifted       = (index << 9).toLong & 0xffffffffL
    val expectedCarry = if (1L >= shifted) 1 else 0
    val result        = 1 - shifted.toInt
    val overflow      = ((1 ^ shifted.toInt) & (1 ^ result)) < 0
    val packed        = (if (result == 0) 0x1000 else 0) | (if (result < 0) 0x2000 else 0) |
      (expectedCarry << 14) | (if (overflow) 0x8000 else 0)
    val flags: Vec[(String, String, Option[String])] = if (sm == 35) Vec.nonEmpty(
      ("P2R", "R7,RZ,0xf000", None),
      ("LOP.XOR", s"R7,R7,$packed", None),
      ("LOP.OR", "R6,R6,R7", None)
    )
    else Vec.empty
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", s"R0,$index", None),
        ("ISCADD", "R0.CC,-R0,c[0x0][0x174],9", None),
        ("IADD.X", "R6,RZ,RZ", None),
        ("IADD", s"R0,R0,${(shifted - 1).toInt}", None),
        ("IADD", "R6,R6,R0", None)
      ) ++ flags ++ Vec.nonEmpty(
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(expectedCarry),
      sm = sm
    )
  }

  test("sm_70 NANOSLEEP.WARP preserves following memory effects") {
    executeSynthetic(
      Vec.nonEmpty(
        ("NANOSLEEP.WARP", "450", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("MOV", "R6,29", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(29),
      sm = 70
    )
  }

  test("sm_75 LDS.U combines scaled lane and uniform offsets for a full word") {
    executeSynthetic(
      Vec.nonEmpty(
        ("MOV", "R0,7", None),
        ("UMOV", "UR5,12", None),
        ("MOV", "R6,0x89abcdef", None),
        ("STS", "[0x40],R6", None),
        ("LDS.U", "R6,[R0.X4+UR5+0x18]", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0x89abcdef),
      shared = 128,
      sm = 75
    )
  }

  for {
    signed  <- Vec.nonEmpty(false, true)
    value   <- Vec.nonEmpty(Long.MinValue, -1L, 0L, 3583L, 3585L, Long.MaxValue)
    minimum <- Vec.nonEmpty(false, true)
  } test(s"sm_120 uniform 64-bit min/max signed=$signed value=$value minimum=$minimum") {
    val compare  = if (signed) java.lang.Long.compare(value, 3584L) else java.lang.Long.compareUnsigned(value, 3584L)
    val expected = if ((compare <= 0) == minimum) value else 3584L
    val select   = if (minimum) "UPT" else "!UPT"
    val width    = if (signed) "S64" else "U64"
    executeSynthetic(
      Vec.nonEmpty(
        ("UMOV", s"UR20,${value.toInt}", None),
        ("UMOV", s"UR21,${(value >>> 32).toInt}", None),
        (s"UIMNMX.$width", s"UR20,UR20,3584,$select,!UPT", None),
        ("MOV", "R6,UR20", None),
        ("MOV", "R7,UR21", None),
        ("LOP.XOR", s"R7,R7,${(expected >>> 32).toInt}", None),
        ("LOP.XOR", s"R6,R6,${expected.toInt}", None),
        ("LOP.OR", "R6,R6,R7", None),
        ("LDC.64", "R2,c[0x0][0x160]", None),
        ("STG.E", "[R2.64],R6", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(0),
      sm = 120
    )
  }
}
