package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

class LegacyRangeReductionSuite extends SyntheticSASSExecutionSuiteBase {
  for {
    sm    <- Vec.nonEmpty(35, 50, 60)
    input <- Vec.nonEmpty(0.0f, -0.0f, 1.0f, -1.0f, Math.PI.toFloat, 1e10f)
  } test(s"sm_$sm reduction captures radians before aliases are overwritten: $input") {
    executeSynthetic(
      Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", s"R4,0x${java.lang.Float.floatToRawIntBits(input).toHexString}", None),
        ("RRO.SINCOS", "R4,R4", None),
        ("MUFU.SIN", "R5,R4", None),
        ("MUFU.COS", "R4,R4", None),
        ("STG.E", "[R10],R5", None),
        ("STG.E", "[R10+4],R4", None),
        ("EXIT", "", None)
      ),
      Vec.nonEmpty(
        java.lang.Float.floatToRawIntBits(Math.sin(input.toDouble).toFloat),
        java.lang.Float.floatToRawIntBits(Math.cos(input.toDouble).toFloat)
      ),
      sm = sm
    )
  }

  for (
    (name, middle) <- Vec.nonEmpty(
      "integer escape"         -> ("MOV", "R5,R4", None),
      "integer min/max escape" -> ("IMNMX", "R4,R4,R5,PT", None),
      "memory escape"          -> ("STG.E", "[R10],R4", None),
      "implicit pair escape"   -> ("DADD", "R6,R3,R8", None),
      "address pair escape"    -> ("LDG.E", "R6,[R3]", None),
      "branch across lifetime" -> ("BRA", "0x30", None),
      "guarded consumer"       -> ("MUFU.SIN", "R5,R4", Some("@P0"))
    )
  ) test(s"opaque range reduction rejects $name") {
    val body: Vec[(String, String, Option[String])] = Vec.nonEmpty(
      ("RRO.SINCOS", "R4,R4", None),
      middle,
      ("MUFU.COS", "R4,R4", None),
      ("EXIT", "", None)
    )
    val function = DecodedFunction(
      "reduction",
      body.zipWithIndex.map { case ((op, operands, pred), index) =>
        SASSFixture.instruction(index * 16, op, operands.split(',').toVec, pred)
      }
    )
    intercept[IllegalArgumentException](VMRangeReduction.inputs(35, function))
  }

  test("incoming control flow after the final consumer does not expose the opaque result") {
    val body = Vec.nonEmpty(
      SASSFixture.instruction(0, "RRO.SINCOS", Vec.nonEmpty("R4", "R4")),
      SASSFixture.instruction(16, "MUFU.COS", Vec.nonEmpty("R6", "R4")),
      SASSFixture.instruction(32, "MOV", Vec.nonEmpty("R4", "RZ")),
      SASSFixture.instruction(48, "BRA", Vec.nonEmpty("0x20"))
    )
    assertEquals(VMRangeReduction.inputs(50, DecodedFunction("reduction", body)).keySet, Set(0, 16))
  }

  test("a branch cannot enter a fused reduction after its producer") {
    val body = Vec.nonEmpty(
      SASSFixture.instruction(0, "BRA", Vec.nonEmpty("0x20")),
      SASSFixture.instruction(16, "RRO.SINCOS", Vec.nonEmpty("R4", "R4")),
      SASSFixture.instruction(32, "MUFU.COS", Vec.nonEmpty("R4", "R4")),
      SASSFixture.instruction(48, "EXIT", Vec.empty)
    )
    intercept[IllegalArgumentException](VMRangeReduction.inputs(35, DecodedFunction("reduction", body)))
  }
  for (sm <- Vec.nonEmpty(50, 60)) test(s"sm_$sm integer min/max ends a consumed range reduction lifetime") {
    executeSynthetic(
      body = Vec.nonEmpty(
        ("LDC.64", "R10,c[0x0][0x160]", None),
        ("MOV", "R4,0", None),
        ("MOV", "R5,2", None),
        ("RRO.SINCOS", "R4,R4", None),
        ("MUFU.COS", "R6,R4", None),
        ("IMNMX", "R4,R5,3,PT", None),
        ("STG.E", "[R10],R6", None),
        ("STG.E", "[R10+4],R4", None),
        ("EXIT", "", None)
      ),
      expected = Vec.nonEmpty(0x3f800000, 2),
      sm = sm
    )
  }

}
